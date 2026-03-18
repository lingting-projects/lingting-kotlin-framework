package live.lingting.framework.crypto.basic

/**
 * @author lingting 2026/3/17 17:41
 */
interface MixinBasic : FinalBasic, IncrementBasic {

    override fun close() {
        //
    }

    override fun calculate(v: ByteArray, offset: Int, len: Int): ByteArray {
        update(v, offset, len)
        return calculate()
    }

}
