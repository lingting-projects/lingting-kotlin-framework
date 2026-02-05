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
    fun ByteArray.toBase64String(): String {
        return Base64.encode(this)
    }

    /**
     * base64字符串转字节码
     */
    @JvmStatic
    fun String.toBase64Bytes(): ByteArray {
        return Base64.decode(this)
    }

}
