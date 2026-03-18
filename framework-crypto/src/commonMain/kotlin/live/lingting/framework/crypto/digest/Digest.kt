package live.lingting.framework.crypto.digest

import live.lingting.framework.crypto.basic.MixinBasic

/**
 * @author lingting 2026/3/17 18:00
 */
abstract class Digest : MixinBasic {

    internal val type: Type

    internal constructor(type: Type) {
        this.type = type
    }

    internal val digester: PlatformDigest by lazy { digester() }

    internal fun digester(): PlatformDigest {
        return PlatformDigest(type)
    }

    override fun update(v: ByteArray, offset: Int, len: Int) {
        digester.update(v, offset, len)
    }

    override fun calculate(): ByteArray {
        return digester.calculate()
    }

    internal enum class Type {

        MD5,

        SHA1,

        SHA256,

        SHA512,

        ;

    }
}

internal expect class PlatformDigest : MixinBasic {

    constructor(type: Digest.Type)

    override fun update(v: ByteArray, offset: Int, len: Int)
    override fun calculate(): ByteArray
    override fun close()
}
