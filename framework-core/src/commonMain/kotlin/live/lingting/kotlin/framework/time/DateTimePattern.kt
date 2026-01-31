package live.lingting.kotlin.framework.time

import kotlinx.datetime.FixedOffsetTimeZone
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.format.char
import kotlin.jvm.JvmField

/**
 * @author lingting
 */
object DateTimePattern {

    @JvmField
    val UTC8_ZONE: TimeZone = FixedOffsetTimeZone(UtcOffset(8))

    @JvmField
    val GMT_ZONE: TimeZone = FixedOffsetTimeZone(UtcOffset(0))

    @JvmField
    val SYSTEM_ZONE: TimeZone = TimeZone.currentSystemDefault()

    // region jvm 日期模式字符串
    const val PTTERN_YMD_HMS: String = "yyyy-MM-dd HH:mm:ss"

    const val PTTERN_YMD_HMS_MILLIS: String = "yyyy-MM-dd HH:mm:ss.SSS"

    const val PTTERN_YMD: String = "yyyy-MM-dd"

    const val PTTERN_HMS: String = "HH:mm:ss"

    const val PTTERN_HMS_MILLIS: String = "HH:mm:ss.SSS"

    const val PTTERN_ISO_8601: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"

    const val PTTERN_ISO_8601_MILLIS: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    // endregion

    // yyyy-MM-dd
    val FORMATTER_YMD = LocalDate.Format {
        year(); char('-'); monthNumber(); char('-'); day()
    }

    // HH:mm:ss
    val FORMATTER_HMS = LocalTime.Format {
        hour(); char(':'); minute(); char(':'); second()
    }

    // HH:mm:ss.SSS
    val FORMATTER_HMS_MILLIS = LocalTime.Format {
        hour(); char(':'); minute(); char(':'); second(); char('.'); secondFraction(3)
    }

    // yyyy-MM-dd HH:mm:ss
    val FORMATTER_YMD_HMS = LocalDateTime.Format {
        date(FORMATTER_YMD)
        char(' ')
        time(FORMATTER_HMS)
    }

    // yyyy-MM-dd HH:mm:ss.SSS
    val FORMATTER_YMD_HMS_MILLIS = LocalDateTime.Format {
        date(FORMATTER_YMD)
        char(' ')
        time(FORMATTER_HMS_MILLIS)
    }

    // yyyy-MM-dd'T'HH:mm:ss'Z' (ISO 8601)
    val FORMATTER_ISO_8601 = LocalDateTime.Format {
        date(FORMATTER_YMD)
        char('T')
        time(FORMATTER_HMS)
        char('Z')
    }

    // yyyy-MM-dd'T'HH:mm:ss.SSS'Z' (ISO 8601 Millis)
    val FORMATTER_ISO_8601_MILLIS = LocalDateTime.Format {
        date(FORMATTER_YMD)
        char('T')
        time(FORMATTER_HMS_MILLIS)
        char('Z')
    }

}

