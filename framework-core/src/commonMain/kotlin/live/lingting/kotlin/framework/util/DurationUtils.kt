package live.lingting.kotlin.framework.util

import kotlin.jvm.JvmStatic
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.DurationUnit.DAYS
import kotlin.time.DurationUnit.MILLISECONDS
import kotlin.time.toDuration

/**
 * @author lingting 2025/12/26 15:47
 */
object DurationUtils {

    const val DAY_WEEK = 7

    const val DAY_MONTH = 30

    const val DAY_YEAR = 365

    /**
     * 浮点数使用 double 运行
     * 实际值为 n * i
     * @param n 原始值
     * @param i 倍率
     * @param unit 目标单位
     */
    @JvmStatic
    fun duration(n: Number, i: Double, unit: DurationUnit): Duration {
        val v = n.toDouble() * i
        return v.toDuration(unit)
    }

    /**
     * 处理整数逻辑：如果 n 也是整数，则使用 Long 运算避免精度/溢出风险
     */
    @JvmStatic
    fun duration(n: Number, i: Long, unit: DurationUnit): Duration {
        // 如果 n 是小数，还是得走 Double 逻辑
        if (n is Float || n is Double) {
            return duration(n, i.toDouble(), unit)
        }
        // 纯整数运算，保护 Long 的精度
        val v = n.toLong() * i
        return v.toDuration(unit)
    }

    @JvmStatic
    fun duration(n: Number, i: Int, unit: DurationUnit): Duration = duration(n, i.toLong(), unit)

    /**
     * 浮点数使用 double 运行, 整数使用long运行
     */
    @JvmStatic
    fun Number.duration(unit: DurationUnit): Duration = duration(this, 1, unit)

    @JvmStatic
    inline val Number.millis: Duration get() = this.duration(MILLISECONDS)

    @JvmStatic
    inline val Number.seconds: Duration get() = this.duration(DurationUnit.SECONDS)

    @JvmStatic
    inline val Number.minutes: Duration get() = this.duration(DurationUnit.MINUTES)

    @JvmStatic
    inline val Number.hours: Duration get() = this.duration(DurationUnit.HOURS)

    @JvmStatic
    inline val Number.days: Duration get() = this.duration(DAYS)

    /**
     * 等价于7天
     */
    @JvmStatic
    inline val Number.weeks: Duration get() = duration(this, DAY_WEEK, DAYS)

    /**
     * 等价于30天
     */
    @JvmStatic
    inline val Number.months: Duration get() = duration(this, DAY_MONTH, DAYS)

    /**
     * 等价于365天
     */
    @JvmStatic
    inline val Number.years: Duration get() = duration(this, DAY_YEAR, DAYS)

}
