package live.lingting.framework.crypto.basic

import live.lingting.framework.util.Base64Utils.toBase64String
import live.lingting.framework.util.StringUtils.toHexString

/**
 * @author lingting 2026/3/17 15:21
 */
interface IncrementBasic {

    fun update(v: String) {
        return update(v.encodeToByteArray())
    }

    fun update(v: ByteArray) {
        return update(v, 0, v.size)
    }

    fun update(v: ByteArray, offset: Int, len: Int)

    fun calculate(): ByteArray

    fun calculateString(): String = calculate().toString()

    fun calculateBase64(): String = calculate().toBase64String()

    fun calculateHex(): String = calculate().toHexString()

}
