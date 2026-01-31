package live.lingting.kotlin.framework.time

import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class StopWatch(
    private val timeSource: TimeSource = TimeSource.Monotonic
) {

    private var startTimeMark: TimeMark? = null

    /**
     * 已累计完成的耗时
     */
    private var accumulatedDuration: Duration = Duration.ZERO

    val isRunning: Boolean
        get() = startTimeMark != null

    /**
     * 开始计时。如果已经在运行，则不做任何操作。
     */
    fun start() {
        if (isRunning) return
        // 标记当前时间点
        startTimeMark = timeSource.markNow()
    }

    /**
     * 停止计时，并将当前段的耗时累加到总时长中
     */
    fun stop() {
        val start = startTimeMark
        if (start != null) {
            accumulatedDuration += start.elapsedNow()
        }
        startTimeMark = null
    }

    /**
     * 重置计时器
     */
    fun reset() {
        startTimeMark = null
        accumulatedDuration = Duration.ZERO
    }

    fun restart() {
        reset()
        start()
    }

    /**
     * 获取总执行时长：已完成时长 + (如果正在运行)当前段时长
     */
    fun duration(): Duration {
        val currentSegment = startTimeMark?.elapsedNow() ?: Duration.ZERO
        return accumulatedDuration + currentSegment
    }

    fun timeNanos(): Long = duration().inWholeNanoseconds

    fun timeMillis(): Long = duration().inWholeMilliseconds
}
