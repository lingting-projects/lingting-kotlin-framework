package live.lingting.framework.crypto.mac

import live.lingting.framework.crypto.basic.MixinBasic

/**
 * @author lingting 2026/2/4 19:55
 */
abstract class Mac(val key: ByteArray) : MixinBasic {

    open fun useKey(k: String): Mac {
        return useKey(k.encodeToByteArray())
    }

    abstract fun useKey(k: ByteArray): Mac

    internal val macer: Platform by lazy { macer() }

    internal abstract fun macer(): Platform

    override fun update(v: ByteArray, offset: Int, len: Int) {
        macer.update(v, offset, len)
    }

    override fun calculate(): ByteArray {
        return macer.calculate()
    }

    internal interface Platform : MixinBasic {

        val key: ByteArray

    }

}
