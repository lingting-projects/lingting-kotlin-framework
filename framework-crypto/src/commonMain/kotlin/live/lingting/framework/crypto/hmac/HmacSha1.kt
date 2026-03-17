package live.lingting.framework.crypto.hmac

import org.kotlincrypto.macs.hmac.sha1.HmacSHA1

/**
 * @author lingting 2026/2/4 19:49
 */
class HmacSha1(private val key: ByteArray) : Hmac<HmacSha1>() {

    constructor(key: String) : this(key.encodeToByteArray())

    override fun macer(): org.kotlincrypto.macs.hmac.Hmac {
        return HmacSHA1(key)
    }

    override fun useKey(k: ByteArray): HmacSha1 = HmacSha1(k)

}
