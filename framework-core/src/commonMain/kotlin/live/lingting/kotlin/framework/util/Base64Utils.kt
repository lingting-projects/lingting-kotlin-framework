package live.lingting.kotlin.framework.util

import kotlin.io.encoding.Base64
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2025/12/24 15:28
 */
object Base64Utils {

    /**
     * 字节码转base64字符串
     */
    @JvmStatic
    fun ByteArray.base64(): String {
        return Base64.encode(this)
    }

    /**
     * base64字符串转字节码
     */
    @JvmStatic
    fun String.base64(): ByteArray {
        return Base64.decode(this)
    }

}
