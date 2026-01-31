package live.lingting.kotlin.framework.util

import kotlinx.coroutines.test.runTest
import live.lingting.kotlin.framework.util.DurationUtils.hours
import live.lingting.kotlin.framework.util.DurationUtils.millis
import live.lingting.kotlin.framework.util.DurationUtils.months
import live.lingting.kotlin.framework.util.DurationUtils.weeks
import live.lingting.kotlin.framework.util.DurationUtils.years
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class DurationUtilsTest {

    @Test
    fun `test basic duration units`() = runTest {
        // 测试基础单位扩展属性
        assertEquals(100.milliseconds, 100.millis)
        assertEquals(60.seconds, 60.seconds)
        assertEquals(30.minutes, 30.minutes)
        assertEquals(24.hours, 24.hours)
        assertEquals(7.days, 7.days)
    }

    @Test
    fun `test custom duration logic`() = runTest {
        // 测试扩展单位（周、月、年）
        assertEquals(7.days, 1.weeks)
        assertEquals(14.days, 2.weeks)

        assertEquals(30.days, 1.months)
        assertEquals(60.days, 2.months)

        assertEquals(365.days, 1.years)
        assertEquals(730.days, 2.years)
    }

    @Test
    fun `test duration with multipliers`() = runTest {
        // 测试带倍率的方法 duration(n, i, unit)
        val d1 = DurationUtils.duration(10, 2L, DurationUnit.SECONDS)
        assertEquals(20.seconds, d1)

        val d2 = DurationUtils.duration(1.5, 2.0, DurationUnit.HOURS)
        assertEquals(3.hours, d2)
    }

    @Test
    fun `test precision and overflow protection`() = runTest {
        // 测试整数溢出保护逻辑 (Long 运算)
        val largeDays = 1000000.days
        assertEquals(largeDays, 1000000.days)

        // 测试浮点数运算
        val halfDay = 0.5.days
        assertEquals(12.hours, halfDay)
    }

    @Test
    fun `test mixed types`() = runTest {
        // Float 类型应触发 Double 逻辑
        val f: Float = 1.5f
        assertEquals(90.minutes, f.hours)

        // Int 类型应触发 Long 逻辑
        val i: Int = 2
        assertEquals(48.hours, i.days)
    }
}
