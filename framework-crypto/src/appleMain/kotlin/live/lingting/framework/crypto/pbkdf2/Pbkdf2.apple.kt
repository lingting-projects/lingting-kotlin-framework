package live.lingting.framework.crypto.pbkdf2

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CCKeyDerivationPBKDF
import platform.CoreCrypto.kCCPBKDF2
import platform.CoreCrypto.kCCPRFHmacAlgSHA1
import platform.CoreCrypto.kCCPRFHmacAlgSHA256
import platform.CoreCrypto.kCCPRFHmacAlgSHA512

@OptIn(ExperimentalForeignApi::class)
internal actual fun Pbkdf2.internalcalculate(
    v: CharArray,
    salt: ByteArray
): ByteArray {
    val keyByteLength = keyLength / 8
    val output = ByteArray(keyByteLength)

    val prf = when (type) {
        Pbkdf2.Type.SHA1 -> kCCPRFHmacAlgSHA1
        Pbkdf2.Type.SHA256 -> kCCPRFHmacAlgSHA256
        Pbkdf2.Type.SHA512 -> kCCPRFHmacAlgSHA512
    }

    val finalSalt = salt ?: byteArrayOf()
    val passwordString = v.concatToString()

    finalSalt.usePinned { pinnedSalt ->
        output.usePinned { pinnedOutput ->
            CCKeyDerivationPBKDF(
                algorithm = kCCPBKDF2,
                password = passwordString,
                passwordLen = passwordString.encodeToByteArray().size.toULong(),
                salt = pinnedSalt.addressOf(0).reinterpret(),
                saltLen = finalSalt.size.toULong(),
                prf = prf,
                rounds = iterations.toUInt(),
                derivedKey = pinnedOutput.addressOf(0).reinterpret(),
                derivedKeyLen = keyByteLength.toULong()
            )
        }
    }
    return output
}
