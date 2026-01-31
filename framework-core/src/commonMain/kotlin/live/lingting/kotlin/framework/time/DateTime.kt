package live.lingting.kotlin.framework.time

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * @author lingting 2026/1/30 16:20
 */
object DateTime {

    /**
     * 默认时区
     */
    @JvmField
    var zone: TimeZone = TimeZone.currentSystemDefault()

    private var timeSource: TimeSource = TimeSource.Monotonic

    // 记录校准时的 真实时间点
    private var anchorTimestamp: Long = Clock.System.now().toEpochMilliseconds()

    // 记录校准时的 单调标记
    private var anchorMark: TimeMark = timeSource.markNow()

    @JvmStatic
    fun calibrate(timeSource: TimeSource) {
        this.timeSource = timeSource
        calibrate(Clock.System.now().toEpochMilliseconds())
    }

    /**
     * 校准时间
     * @param realMillis 外部下发的准确毫秒时间戳
     */
    @JvmStatic
    fun calibrate(realMillis: Long) {
        anchorTimestamp = realMillis
        anchorMark = timeSource.markNow()
    }

    @JvmStatic
    fun millis(): Long {
        // 计算从校准那一刻起到现在经过的物理时长
        val elapsed: Duration = anchorMark.elapsedNow()
        return anchorTimestamp + elapsed.inWholeMilliseconds
    }

    @JvmStatic
    fun instant(): Instant = Instant.fromEpochMilliseconds(millis())

    @JvmStatic
    fun current(): LocalDateTime = instant().toLocalDateTime(zone)

    @JvmStatic
    fun date(): LocalDate = current().date

    @JvmStatic
    fun time(): LocalTime = current().time

}
