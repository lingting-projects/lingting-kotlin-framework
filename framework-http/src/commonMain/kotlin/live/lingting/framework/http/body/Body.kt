package live.lingting.framework.http.body

/**
 * @author lingting 2026/1/31 17:41
 */
abstract class Body<T : Any> {

    /**
     * 源 bytes
     */
    abstract fun length(): Long

    abstract fun source(): T

    abstract fun string(): String

    val isEmpty
        get() = length() < 1

}
