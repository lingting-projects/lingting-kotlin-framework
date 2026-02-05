package live.lingting.kotlin.framework.aws

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import live.lingting.kotlin.framework.crypto.util.DigestUtils.toMd5Hex
import live.lingting.kotlin.framework.data.DataSize
import live.lingting.kotlin.framework.time.DateTime
import live.lingting.kotlin.framework.time.DateTimePattern
import live.lingting.kotlin.framework.util.Base64Utils.toBase64String
import live.lingting.kotlin.framework.util.StringUtils.firstUpper
import live.lingting.kotlin.framework.util.StringUtils.toHexBytes
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic


/**
 * @author lingting 2024-09-19 15:20
 */
object AwsUtils {

    @JvmField
    val DATE_FORMATTER = LocalDateTime.Format {
        year(); monthNumber(); day()
        char('T')
        hour(); minute(); second()
        char('Z')
    }

    @JvmField
    val SCOPE_DATE_FORMATTER = LocalDateTime.Format {
        year(); monthNumber(); day()
    }

    /**
     * 10M
     */
    @JvmField
    val MULTIPART_DEFAULT_PART_SIZE = DataSize.ofMb(10)

    /**
     * 5G
     */
    @JvmField
    val MULTIPART_MAX_PART_SIZE = DataSize.ofGb(5)

    /**
     * 100K
     */
    @JvmField
    val MULTIPART_MIN_PART_SIZE = DataSize.ofKb(100)

    const val MULTIPART_MAX_PART_COUNT = 1000L

    const val PAYLOAD_UNSIGNED: String = "UNSIGNED-PAYLOAD"

    const val HEADER_PREFIX: String = "x-amz"

    const val HEADER_DATE: String = "$HEADER_PREFIX-date"

    const val HEADER_CONTENT_SHA256: String = "$HEADER_PREFIX-content-sha256"

    const val HEADER_TOKEN: String = "$HEADER_PREFIX-security-token"

    const val HEADER_ACL: String = "$HEADER_PREFIX-acl"

    const val HEADER_MD5: String = "content-md5"

    const val HEADER_PREFIX_META: String = "$HEADER_PREFIX-meta-"

    @JvmStatic
    fun format(dateTime: LocalDateTime, formatter: DateTimeFormat<LocalDateTime>): String {
        // 当前时区时间
        val atZone = dateTime.toInstant(DateTime.zone)
        // 切换到+0
        val atGmt = atZone.toLocalDateTime(DateTimePattern.GMT_ZONE)
        return formatter.format(atGmt)
    }

    @JvmStatic
    fun parse(string: String, formatter: DateTimeFormat<LocalDateTime>): LocalDateTime {
        // 时间原始字符串转为时间类型
        val dateTime = formatter.parse(string)
        // 原始时区为 +0
        val atGmt = dateTime.toInstant(DateTimePattern.GMT_ZONE)
        // 切换到当前系统时区
        val atZone = atGmt.toLocalDateTime(DateTime.zone)
        return atZone
    }

    @JvmStatic
    fun toParamsKey(key: String): String {
        val split = key.split("-")
        return split.joinToString("-") { it.firstUpper() }
    }

    /**
     * 计算内容在请求头上的 contentMd5 值
     */
    @JvmStatic
    fun contentMd5(bytes: ByteArray): String {
        val hex = bytes.toMd5Hex()
        return contentMd5FromMd5(hex)
    }

    /**
     * 从原始的md5 生成请求头上的 contentMd5 值
     */
    @JvmStatic
    fun contentMd5FromMd5(md5: String): String {
        val bytes = md5.toHexBytes()
        return bytes.toBase64String()
    }

}
