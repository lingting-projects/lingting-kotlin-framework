package live.lingting.framework.crypto.pbkdf2

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

internal actual fun Pbkdf2.internalcalculate(
    v: CharArray,
    salt: ByteArray?
): ByteArray {
    val algorithm = when (this) {
        is Pbkdf2Sha256 -> "PBKDF2WithHmacSHA256"
        is Pbkdf2Sha512 -> "PBKDF2WithHmacSHA512"
        else -> throw UnsupportedOperationException()
    }
    val spec = PBEKeySpec(v, salt, iterations, keyLength)
    val factory = SecretKeyFactory.getInstance(algorithm)
    return factory.generateSecret(spec).encoded
}
