package live.lingting.kotlin.framework.http.body

/**
 * @author lingting 2026/1/31 17:40
 */
object EmptyBody : Body<ByteArray>() {

    override fun length(): Long = 0

    override fun source(): ByteArray = byteArrayOf()

}
