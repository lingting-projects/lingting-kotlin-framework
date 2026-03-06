package live.lingting.framework.crypto.hmac

import org.kotlincrypto.core.mac.Mac
import org.kotlincrypto.macs.hmac.sha2.HmacSHA256

/**
 * @author lingting 2026/2/4 19:49
 */
class HmacSha256(private val key: ByteArray) : Hmac<HmacSha256>() {

    constructor(key: String) : this(key.encodeToByteArray())

    override fun macer(): Mac {
        return HmacSHA256(key)
    }

    override fun useKey(k: ByteArray): HmacSha256 = HmacSha256(k)

}
