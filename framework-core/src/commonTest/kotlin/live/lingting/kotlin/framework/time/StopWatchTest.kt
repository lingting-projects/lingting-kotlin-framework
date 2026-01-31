package live.lingting.kotlin.framework.time

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class StopWatchTest {

    @Test
    fun `test basic start and stop`() = runTest {
        val watch = StopWatch(testTimeSource)

        // 初始状态
        assertFalse(watch.isRunning)
        assertEquals(0, watch.timeMillis())

        // 开始计时
        watch.start()
        assertTrue(watch.isRunning)

        // 模拟耗时
        delay(100.milliseconds)

        // 运行中获取时间 (应大于 0)
        assertTrue(watch.duration() > 0.milliseconds)

        // 停止计时
        watch.stop()
        assertFalse(watch.isRunning)

        val recordedTime = watch.timeMillis()
        assertTrue(recordedTime >= 100, "记录时间应至少为 100ms, 实际为: $recordedTime")

        // 停止后再次获取时间应保持不变
        delay(50)
        assertEquals(recordedTime, watch.timeMillis(), "停止后时长不应继续增长")
    }

    @Test
    fun `test restart`() = runTest {
        val watch = StopWatch(testTimeSource)

        watch.start()
        delay(50)
        val firstRun = watch.timeMillis()

        // 重启
        watch.restart()
        assertTrue(watch.isRunning)
        // 重启后时间应重置（因为 restart 调用了 stop -> start，而 start 中重置了 duration）
        assertTrue(watch.timeMillis() < firstRun)
    }

    @Test
    fun `test duration units`() = runTest {
        val watch = StopWatch(testTimeSource)
        watch.start()
        delay(200)
        watch.stop()

        val nanos = watch.timeNanos()
        val millis = watch.timeMillis()

        // 验证纳秒和毫秒的换算关系
        assertEquals(millis, nanos / 1_000_000)
    }

    @Test
    fun `test start while running`() = runTest {
        val watch = StopWatch(testTimeSource)
        watch.start()
        delay(50)
        val mark1 = watch.duration()

        // 如果已经在运行，再次调用 start 不应重置时间（根据代码逻辑：if (isRunning) return）
        watch.start()
        delay(50)
        val mark2 = watch.duration()

        assertTrue(mark2 > mark1, "再次调用 start 不应重置正在运行的计时器")
    }
}
