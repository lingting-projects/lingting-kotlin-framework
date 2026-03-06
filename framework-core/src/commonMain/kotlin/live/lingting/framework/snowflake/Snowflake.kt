package live.lingting.framework.snowflake

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import live.lingting.framework.time.DateTime
import kotlin.math.max

/**
 * KMP 通用雪花算法实现
 * 使用 atomicfu 保证多线程/多平台安全
 */
open class Snowflake(
    val params: SnowflakeParams = SnowflakeParams.DEFAULT,
    val workerId: Long,
    val datacenterId: Long
) {

    /**
     * 构造函数
     * @param workerId 机器ID
     * @param datacenterId 数据中心ID
     */
    constructor(workerId: Long, datacenterId: Long) : this(SnowflakeParams.DEFAULT, workerId, datacenterId)

    private val _lastTimestamp = atomic(-1L)
    private val _sequence = atomic(0L)

    init {
        require(workerId in 0..params.maxWorkerId) {
            "Worker ID 必须在 0 到 ${params.maxWorkerId} 之间"
        }
        require(datacenterId in 0..params.maxDatacenterId) {
            "Datacenter ID 必须在 0 到 ${params.maxDatacenterId} 之间"
        }
    }

    /**
     * 生成下一个ID (CAS 无锁实现)
     */
    fun nextId(): Long {
        // 使用 atomicfu 的 loop 替代 synchronized
        _lastTimestamp.loop { lastTimestamp ->
            var timestamp = currentTimestamp()

            // 时钟回拨处理
            if (timestamp < lastTimestamp) {
                if (!allowClockBackwards(timestamp, lastTimestamp)) {
                    throw IllegalStateException("时钟回拨！当前: $timestamp; 上次: $lastTimestamp")
                }
                // 允许回拨则使用上次时间
                timestamp = lastTimestamp
            }

            var currentSequence = _sequence.value
            var nextTimestamp = timestamp

            if (lastTimestamp == timestamp) {
                // 同一毫秒内
                val nextSequence = (currentSequence + 1) and params.sequenceMask
                if (nextSequence == 0L) {
                    // 毫秒内序列溢出，阻塞到下一毫秒
                    nextTimestamp = tilNextMillis(lastTimestamp)
                    currentSequence = 0L
                } else {
                    currentSequence = nextSequence
                }
            } else {
                // 不同毫秒，序列重置
                currentSequence = 0L
            }

            // 使用 CAS 尝试更新状态，如果期间有其他线程修改了 lastTimestamp，则 loop 重试
            if (_lastTimestamp.compareAndSet(lastTimestamp, nextTimestamp)) {
                _sequence.value = currentSequence

                // 拼装 ID 并返回
                return ((nextTimestamp - params.startTimestamp) shl params.timestampLeftShift.toInt()) or
                        (datacenterId shl params.datacenterIdShift.toInt()) or
                        (workerId shl params.workerIdShift.toInt()) or
                        currentSequence
            }
        }
    }

    protected open fun allowClockBackwards(current: Long, last: Long): Boolean = false

    private fun tilNextMillis(lastTimestamp: Long): Long {
        var timestamp = currentTimestamp()
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimestamp()
        }
        return timestamp
    }

    protected open fun currentTimestamp(): Long = DateTime.millis()

    fun nextStr(): String = nextId().toString()

    fun nextIds(count: Int): List<Long> {
        val size = max(1, count)
        return List(size) { nextId() }
    }

    fun nextStr(count: Int): List<String> = nextIds(count).map { it.toString() }
}
