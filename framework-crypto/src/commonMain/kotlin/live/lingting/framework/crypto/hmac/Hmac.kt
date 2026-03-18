package live.lingting.framework.crypto.hmac

import live.lingting.framework.crypto.mac.Mac

/**
 * @author lingting 2026/2/4 19:55
 */
abstract class Hmac : Mac {

    internal val type: Type

    internal constructor(key: ByteArray, type: Type) : super(key) {
        this.type = type
    }

    override fun macer(): Platform {
        return PlatformHmac(key, type)
    }

    override fun useKey(k: ByteArray): Hmac = object : Hmac(k, type) {

    }

    internal enum class Type {

        SHA1,

        SHA256,

        SHA512,

        ;

    }

}

internal expect class PlatformHmac : Mac.Platform {

    constructor(key: ByteArray, type: Hmac.Type)

    override val key: ByteArray

    override fun update(v: ByteArray, offset: Int, len: Int)
    override fun calculate(): ByteArray
    override fun close()
}
