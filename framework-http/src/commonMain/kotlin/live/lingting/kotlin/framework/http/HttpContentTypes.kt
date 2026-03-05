package live.lingting.kotlin.framework.http

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2025/6/3 15:30
 */
object HttpContentTypes {

    const val FORM_URL_ENCODE = "application/x-www-form-urlencoded"

    const val JSON = "application/json"

    const val XML = "application/xml"

    const val STREAM = "application/octet-stream"

    @JvmField
    val JSON_UTF8 = jsonCharset("utf8")

    @JvmStatic
    fun jsonCharset(charset: String) = "$JSON; charset=${charset}"

}
