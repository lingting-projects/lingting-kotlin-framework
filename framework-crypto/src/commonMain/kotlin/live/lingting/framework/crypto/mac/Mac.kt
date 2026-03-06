package live.lingting.framework.crypto.mac

import live.lingting.framework.util.Base64Utils.toBase64String
import live.lingting.framework.util.StringUtils.toHexString

/**
 * @author lingting 2026/2/4 19:55
 */
abstract class Mac<T : Mac<T>> {

    open fun useKey(k: String): T {
        return useKey(k.encodeToByteArray())
    }

    abstract fun useKey(k: ByteArray): T

    open fun calculate(v: String): ByteArray {
        return calculate(v.encodeToByteArray())
    }

    abstract fun calculate(v: ByteArray): ByteArray

    open fun calculateString(v: String): String = calculate(v).toString()

    open fun calculateString(v: ByteArray): String = calculate(v).toString()

    open fun calculateBase64(v: String): String = calculate(v).toBase64String()

    open fun calculateBase64(v: ByteArray): String = calculate(v).toBase64String()

    open fun calculateHex(v: String): String = calculate(v).toHexString()

    open fun calculateHex(v: ByteArray): String = calculate(v).toHexString()

}
