package live.lingting.framework.crypto.basic

import live.lingting.framework.util.Base64Utils.toBase64String
import live.lingting.framework.util.StringUtils.toHexString

/**
 * @author lingting 2026/3/17 15:21
 */
interface FinalBasic {

    fun calculate(v: String): ByteArray {
        return calculate(v.encodeToByteArray())
    }

    fun calculate(v: ByteArray): ByteArray {
        return calculate(v, 0, v.size)
    }

    fun calculate(v: ByteArray, offset: Int, len: Int): ByteArray

    fun calculateString(v: String): String = calculate(v).toString()

    fun calculateString(v: ByteArray): String = calculate(v).toString()

    fun calculateBase64(v: String): String = calculate(v).toBase64String()

    fun calculateBase64(v: ByteArray): String = calculate(v).toBase64String()

    fun calculateHex(v: String): String = calculate(v).toHexString()

    fun calculateHex(v: ByteArray): String = calculate(v).toHexString()

}
