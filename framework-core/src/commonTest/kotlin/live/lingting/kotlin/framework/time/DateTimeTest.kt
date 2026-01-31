package live.lingting.kotlin.framework.time

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import kotlinx.datetime.TimeZone
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class DateTimeTest {

    @BeforeTest
    fun setup() {
        DateTime.zone = TimeZone.UTC
    }

    @Test
    fun `test time calibration and progression with TestTimeSource`() = runTest {
        DateTime.calibrate(testTimeSource)
        // 初始时间（假设 calibrate 时系统时间是某一时刻）
        val baseMillis = DateTime.millis()

        // 模拟虚拟时间流逝 500 毫秒
        delay(500.milliseconds)

        val currentMillis = DateTime.millis()
        assertEquals(baseMillis + 500, currentMillis, "虚拟时间流逝后，DateTime 毫秒数应同步增加")
    }

    @Test
    fun `test complex time jump`() = runTest {
        DateTime.calibrate(testTimeSource)
        // 校准到一个固定点
        // 2026-01-30 12:00:00
        val startMillis = 1738238400000L
        DateTime.calibrate(startMillis)

        // 模拟时间流逝 1小时 30分钟
        delay(1.hours)
        delay(30.minutes)

        val currentTime = DateTime.time()
        assertEquals(13, currentTime.hour)
        assertEquals(30, currentTime.minute)
    }

    @Test
    fun `test instant consistency`() = runTest {
        DateTime.calibrate(testTimeSource)
        val startInstant = DateTime.instant()

        // 模拟极短的时间推移
        delay(1.milliseconds)

        val endInstant = DateTime.instant()
        assertTrue(endInstant > startInstant, "即使只有 1ms，Instant 也应该向前推进")
    }

    @Test
    fun `test multiple calibrations`() = runTest {
        DateTime.calibrate(testTimeSource)
        // 第一次校准
        DateTime.calibrate(1000L)
        delay(500.milliseconds)
        assertEquals(1500L, DateTime.millis())

        // 第二次校准（模拟与服务器二次同步）
        // 假设服务器说现在是 2000L
        DateTime.calibrate(2000L)
        assertEquals(2000L, DateTime.millis())

        delay(100.milliseconds)
        assertEquals(2100L, DateTime.millis())
    }

}
