package live.lingting.framework.http.body

/**
 * @author lingting 2026/1/31 17:40
 */
object EmptyBody : live.lingting.framework.http.body.Body<ByteArray>() {

    override fun length(): Long = 0

    override fun source(): ByteArray = byteArrayOf()

    override fun string(): String = ""

}
