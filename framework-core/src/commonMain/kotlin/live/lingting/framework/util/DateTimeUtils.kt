package live.lingting.framework.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import live.lingting.framework.time.DateTime
import kotlin.jvm.JvmStatic
import kotlin.time.Instant

/**
 * @author lingting 2026/3/5 19:53
 */
object DateTimeUtils {

    @JvmStatic
    fun LocalDateTime.timestamp(zone: TimeZone = DateTime.zone): Long = toInstant(zone).toEpochMilliseconds()

    @JvmStatic
    fun Long.dateTime(zone: TimeZone = DateTime.zone): LocalDateTime =
        Instant.fromEpochSeconds(this).toLocalDateTime(zone)

}
