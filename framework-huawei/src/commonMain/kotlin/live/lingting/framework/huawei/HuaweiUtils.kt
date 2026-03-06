package live.lingting.framework.huawei

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.util.DurationUtils.days
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/**
 * @author lingting 2024-09-13 11:54
 */
object HuaweiUtils {

    /**
     * rfc_1123 时间部分格式
     */
    @JvmField
    val FORMATTER_RFC_1123_DATETIME: DateTimeFormat<LocalDateTime> = LocalDateTime.Format {
        dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED)
        chars(", ")
        day(Padding.NONE)
        char(' ')
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        char(' ')
        year()
        char(' ')
        hour()
        char(':')
        minute()
        char(':')
        second()
    }

    val TOKEN_EARLY_EXPIRE: Duration = 15.minutes

    val CREDENTIAL_EXPIRE: Duration = 1.days

    const val HEADER_DATE: String = "Date"

    /**
     * 把 YYYY-MM-DDTHH:mm:ss.ssssssZ 格式字符串转为指定时区的时间
     */
    @JvmStatic
    fun parse(str: String, zone: TimeZone): LocalDateTime {
        val instant = Instant.parse(str)
        return instant.toLocalDateTime(zone)
    }

    @JvmStatic
    fun format(dateTime: LocalDateTime): String {
        // 时间区域
        val format = AwsUtils.format(dateTime, FORMATTER_RFC_1123_DATETIME)
        // 拼接时区
        return "$format GMT"
    }

    @JvmStatic
    fun parse(string: String): LocalDateTime {
        // 格式通常是 "Day, DD Mon YYYY HH:mm:ss ZONE"
        val lastSpaceIndex = string.lastIndexOf(' ')
        // 时间区域
        val timePart = string.substring(0, lastSpaceIndex)
        // 时区区域
        val zonePart = string.substring(lastSpaceIndex + 1)
        val zone = TimeZone.of(zonePart)
        return AwsUtils.parse(
            zone,
            timePart,
            FORMATTER_RFC_1123_DATETIME
        )
    }

}
