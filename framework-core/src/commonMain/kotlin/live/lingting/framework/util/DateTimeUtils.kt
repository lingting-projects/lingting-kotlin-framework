package live.lingting.framework.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import live.lingting.framework.time.DateTime
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.time.Instant

/**
 * @author lingting 2026/3/5 19:53
 */
object DateTimeUtils {

    @JvmField
    val TIME_START = LocalTime(0, 0, 0)

    inline val LocalTime.Companion.START get() = TIME_START

    @JvmStatic
    fun LocalDate.atStartOfDay(): LocalDateTime = LocalDateTime(this, LocalTime.START)

    @JvmStatic
    fun LocalDateTime.timestamp(zone: TimeZone = DateTime.zone): Long = toInstant(zone).toEpochMilliseconds()

    @JvmStatic
    fun Long.dateTime(zone: TimeZone = DateTime.zone): LocalDateTime =
        Instant.fromEpochMilliseconds(this).toLocalDateTime(zone)

}
