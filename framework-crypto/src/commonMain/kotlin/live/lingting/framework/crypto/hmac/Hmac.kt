package live.lingting.framework.crypto.hmac

import live.lingting.framework.crypto.mac.Mac

/**
 * @author lingting 2026/2/4 19:55
 */
abstract class Hmac<T : Hmac<T>> : Mac<T>() {

    protected val macer: org.kotlincrypto.core.mac.Mac by lazy { macer() }

    protected abstract fun macer(): org.kotlincrypto.core.mac.Mac

    override fun calculate(v: ByteArray): ByteArray {
        return macer.doFinal(v)
    }

}
