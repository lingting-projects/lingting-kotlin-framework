package live.lingting.framework.crypto.hmac

import live.lingting.framework.crypto.extra.mac
import live.lingting.framework.crypto.mac.Mac

internal actual class PlatformHmac : Mac.Platform {

    actual override val key: ByteArray

    val mac: javax.crypto.Mac

    actual constructor(key: ByteArray, type: Hmac.Type) {
        this.key = key
        val algorithm = when (type) {
            Hmac.Type.SHA1 -> "HmacSHA1"
            Hmac.Type.SHA256 -> "HmacSHA256"
            Hmac.Type.SHA512 -> "HmacSHA512"
        }
        this.mac = mac(algorithm, key)
    }

    actual override fun update(v: ByteArray, offset: Int, len: Int) {
        mac.update(v, offset, len)
    }

    actual override fun calculate(): ByteArray {
        return mac.doFinal()
    }

}
