package live.lingting.framework.crypto.digest

import live.lingting.framework.crypto.basic.MixinBasic
import java.security.MessageDigest

internal actual class PlatformDigest : MixinBasic {

    val digester: MessageDigest

    actual constructor(type: Digest.Type) {
        val algorithm = when (type) {
            Digest.Type.MD5 -> "MD5"
            Digest.Type.SHA1 -> "SHA-1"
            Digest.Type.SHA256 -> "SHA-256"
            Digest.Type.SHA512 -> "SHA-512"
        }
        this.digester = MessageDigest.getInstance(algorithm)
    }

    actual override fun update(v: ByteArray, offset: Int, len: Int) {
        digester.update(v, offset, len)
    }

    actual override fun calculate(): ByteArray {
        return digester.digest()
    }

}
