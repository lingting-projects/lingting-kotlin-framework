package live.lingting.framework.crypto.pbkdf2

import korlibs.crypto.PBKDF2

internal actual fun Pbkdf2.internalcalculate(
    v: CharArray,
    salt: ByteArray
): ByteArray {
    val str = v.concatToString()
    val bytes = str.encodeToByteArray()
    val hash = when (type) {
        Pbkdf2.Type.SHA1 -> PBKDF2.pbkdf2WithHmacSHA1(bytes, salt, iterations, keyLength)
        Pbkdf2.Type.SHA256 -> PBKDF2.pbkdf2WithHmacSHA256(bytes, salt, iterations, keyLength)
        Pbkdf2.Type.SHA512 -> PBKDF2.pbkdf2WithHmacSHA512(bytes, salt, iterations, keyLength)
    }
    return hash.bytes
}
