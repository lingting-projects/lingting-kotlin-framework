package live.lingting.framework.crypto.pbkdf2

import live.lingting.framework.crypto.basic.FinalBasic
import live.lingting.framework.util.Base64Utils.toBase64String

/**
 * @author lingting 2026/3/17 15:40
 */
abstract class Pbkdf2 : FinalBasic {

    val salt: ByteArray
    internal val type: Type

    internal constructor(salt: ByteArray, type: Type) {
        this.salt = salt
        this.type = type
    }

    var iterations = 100000

    var keyLength = 256

    open fun useSalt(k: String): Pbkdf2 {
        return useSalt(k.encodeToByteArray())
    }

    open fun useSalt(k: ByteArray): Pbkdf2 {
        return object : Pbkdf2(k, type) {

        }.also {
            it.iterations = iterations
            it.keyLength = keyLength
        }
    }

    override fun calculate(v: ByteArray, offset: Int, len: Int): ByteArray {
        val array = v.decodeToString(offset, len).toCharArray()
        return internalcalculate(array, salt)
    }

    override fun calculate(v: String): ByteArray {
        val array = v.toCharArray()
        return internalcalculate(array, salt)
    }

    override fun calculateString(v: String): String {
        val array = v.toCharArray()
        return internalcalculate(array, salt).toString()
    }

    override fun calculateBase64(v: String): String {
        val array = v.toCharArray()
        return internalcalculate(array, salt).toBase64String()
    }

    override fun calculateHex(v: String): String {
        val array = v.toCharArray()
        return internalcalculate(array, salt).toHexString()
    }

    internal enum class Type {

        SHA1,

        SHA256,

        SHA512,

        ;

    }

}

internal expect fun Pbkdf2.internalcalculate(v: CharArray, salt: ByteArray): ByteArray
