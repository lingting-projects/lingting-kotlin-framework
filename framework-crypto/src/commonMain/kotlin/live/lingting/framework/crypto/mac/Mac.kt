package live.lingting.framework.crypto.mac

import live.lingting.framework.crypto.basic.FinalBasic
import live.lingting.framework.crypto.basic.IncrementBasic

/**
 * @author lingting 2026/2/4 19:55
 */
abstract class Mac<T : Mac<T>> : IncrementBasic, FinalBasic {

    open fun useKey(k: String): T {
        return useKey(k.encodeToByteArray())
    }

    abstract fun useKey(k: ByteArray): T

    protected val macer: org.kotlincrypto.core.mac.Mac by lazy { macer() }

    protected abstract fun macer(): org.kotlincrypto.core.mac.Mac

    override fun update(v: ByteArray, offset: Int, len: Int) {
        macer.update(v, offset, len)
    }

    override fun calculate(): ByteArray {
        return macer.doFinal()
    }

    override fun calculate(v: ByteArray, offset: Int, len: Int): ByteArray {
        if (offset == 0 && len == v.size) {
            return macer.doFinal(v)
        }
        macer.update(v, offset, len)
        return macer.doFinal()
    }

}
