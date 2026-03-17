package live.lingting.framework.crypto.hmac

import org.kotlincrypto.macs.hmac.sha2.HmacSHA512

/**
 * @author lingting 2026/2/4 19:49
 */
class HmacSha512(private val key: ByteArray) : Hmac<HmacSha512>() {

    constructor(key: String) : this(key.encodeToByteArray())

    override fun macer(): org.kotlincrypto.macs.hmac.Hmac {
        return HmacSHA512(key)
    }

    override fun useKey(k: ByteArray): HmacSha512 = HmacSha512(k)

}
