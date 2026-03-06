package live.lingting.framework.snowflake

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Snowflake 核心逻辑测试
 * 兼容所有 KMP 平台 (JVM, Native, JS)
 */
class SnowflakeTest {

    // 1. 基础唯一性与递增性测试
    @Test
    fun testGenerateUniqueAndIncreasingIds() {
        val snowflake = Snowflake(workerId = 1, datacenterId = 1)
        val count = 1000
        val idSet = mutableSetOf<Long>()

        var lastId = -1L
        repeat(count) {
            val currentId = snowflake.nextId()

            // 验证唯一性
            assertTrue(idSet.add(currentId), "检测到重复 ID: $currentId")

            // 验证趋势递增
            assertTrue(currentId > lastId, "ID 必须大于前一个 ID. 当前: $currentId, 上次: $lastId")
            lastId = currentId
        }

        assertEquals(count, idSet.size)
    }

    // 2. 位运算准确性测试 (验证 workerId 和 datacenterId 是否正确嵌入)
    @Test
    fun testBitOffsets() {
        val params = SnowflakeParams.DEFAULT
        val expectedWorkerId = 15L
        val expectedDatacenterId = 25L

        val snowflake = Snowflake(params, expectedWorkerId, expectedDatacenterId)
        val id = snowflake.nextId()

        // 使用位掩码反向提取
        val actualWorkerId = (id shr params.workerIdShift.toInt()) and params.maxWorkerId
        val actualDatacenterId = (id shr params.datacenterIdShift.toInt()) and params.maxDatacenterId

        assertEquals(expectedWorkerId, actualWorkerId, "Worker ID 提取不匹配")
        assertEquals(expectedDatacenterId, actualDatacenterId, "Datacenter ID 提取不匹配")
    }

    // 3. 边界值检查
    @Test
    fun testConstructorBounds() {
        val params = SnowflakeParams.DEFAULT

        // 测试越界 workerId (默认 5bit 最大 31)
        assertFailsWith<IllegalArgumentException> {
            Snowflake(workerId = params.maxWorkerId + 1, datacenterId = 0)
        }

        // 测试负数
        assertFailsWith<IllegalArgumentException> {
            Snowflake(workerId = -1, datacenterId = 0)
        }
    }

    // 4. 时钟回拨模拟测试
    @Test
    fun testClockBackwardsBehavior() {
        // 定义一个模拟时间的子类
        class MockSnowflake(var mockTime: Long) : Snowflake(workerId = 1, datacenterId = 1) {
            override fun currentTimestamp(): Long = mockTime

            // 默认不允许回拨
            var allowBackwards = false
            override fun allowClockBackwards(current: Long, last: Long): Boolean = allowBackwards
        }

        val startTime = 1000000L
        val snowflake = MockSnowflake(startTime)

        // 第一条正常
        snowflake.nextId()

        // 模拟回拨 100ms
        snowflake.mockTime = startTime - 100

        // 场景 A: 不允许回拨时抛出异常
        assertFailsWith<IllegalStateException>("应当抛出时钟回拨异常") {
            snowflake.nextId()
        }

        // 场景 B: 允许回拨时正常生成 (使用上次时间戳)
        snowflake.allowBackwards = true
        val idAfterBackwards = snowflake.nextId()

        // 提取时间戳部分验证 (右移 22 位，加上开始时间)
        val idTimestamp =
            (idAfterBackwards shr SnowflakeParams.DEFAULT.timestampLeftShift.toInt()) + SnowflakeParams.DEFAULT.startTimestamp
        assertEquals(startTime, idTimestamp, "允许回拨时，生成的 ID 时间戳应等于上次记录的时间戳")
    }

    // 5. 序列号溢出测试 (同一毫秒内生成超过 4096 个 ID)
    @Test
    fun testSequenceOverflowInSameMillis() {
        // 模拟固定时间的 Snowflake
        class ConstantTimeSnowflake(val constantTime: Long) : Snowflake(workerId = 1, datacenterId = 1) {
            private var callCount = 0
            override fun currentTimestamp(): Long {
                // 前 4097 次调用返回相同时间，之后返回下一毫秒
                return if (callCount++ < 5000) constantTime else constantTime + 1
            }
        }

        val snowflake = ConstantTimeSnowflake(2000000L)

        // 连续生成超过 sequenceMask (4095) 的数量
        // 如果逻辑正确，tilNextMillis 会被触发，确保 ID 不重复
        val ids = List(4100) { snowflake.nextId() }
        assertEquals(4100, ids.distinct().size, "同一毫秒溢出时产生了重复 ID")
    }
}
