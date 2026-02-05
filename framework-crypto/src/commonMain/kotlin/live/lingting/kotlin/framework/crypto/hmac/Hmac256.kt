package live.lingting.kotlin.framework.crypto.hmac

import org.kotlincrypto.macs.hmac.sha2.HmacSHA256

/**
 * @author lingting 2026/2/4 19:49
 */
class Hmac256 : Hmac<Hmac256> {

    val macer: HmacSHA256

    constructor(key: String) : this(key.encodeToByteArray())

    constructor(key: ByteArray) {
        macer = HmacSHA256(key)
    }

    override fun useKey(k: ByteArray): Hmac256 = Hmac256(k)

    override fun calculate(v: ByteArray): ByteArray {
        return macer.doFinal(v)
    }

}
