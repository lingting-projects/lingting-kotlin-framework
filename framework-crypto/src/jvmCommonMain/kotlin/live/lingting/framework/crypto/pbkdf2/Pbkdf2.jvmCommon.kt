package live.lingting.framework.crypto.pbkdf2

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

internal actual fun Pbkdf2.internalcalculate(
    v: CharArray,
    salt: ByteArray
): ByteArray {
    val algorithm = when (type) {
        Pbkdf2.Type.SHA1 -> "PBKDF2WithHmacSHA1"
        Pbkdf2.Type.SHA256 -> "PBKDF2WithHmacSHA256"
        Pbkdf2.Type.SHA512 -> "PBKDF2WithHmacSHA512"
    }
    val spec = PBEKeySpec(v, salt, iterations, keyLength)
    val factory = SecretKeyFactory.getInstance(algorithm)
    return factory.generateSecret(spec).encoded
}
