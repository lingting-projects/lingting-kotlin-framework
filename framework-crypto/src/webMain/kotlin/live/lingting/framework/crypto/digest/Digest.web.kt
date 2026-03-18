package live.lingting.framework.crypto.digest

import korlibs.crypto.Hasher
import korlibs.crypto.MD5
import korlibs.crypto.SHA1
import korlibs.crypto.SHA256
import korlibs.crypto.SHA512
import live.lingting.framework.crypto.basic.MixinBasic

internal actual class PlatformDigest : MixinBasic {

    val hasher: Hasher

    actual constructor(type: Digest.Type) {
        this.hasher = when (type) {
            Digest.Type.MD5 -> MD5()
            Digest.Type.SHA1 -> SHA1()
            Digest.Type.SHA256 -> SHA256()
            Digest.Type.SHA512 -> SHA512()
        }
    }

    actual override fun update(v: ByteArray, offset: Int, len: Int) {
        hasher.update(v, offset, len)
    }

    actual override fun calculate(): ByteArray {
        val hash = hasher.digest()
        return hash.bytes
    }

}
