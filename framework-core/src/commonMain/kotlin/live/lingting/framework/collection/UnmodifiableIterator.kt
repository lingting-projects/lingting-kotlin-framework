package live.lingting.framework.collection

/**
 * @author lingting 2026/2/6 16:29
 */
class UnmodifiableIterator<T>(private val source: Iterator<T>) : MutableIterator<T> {

    override fun remove() = throw UnsupportedOperationException()

    override fun next(): T = source.next()

    override fun hasNext(): Boolean = source.hasNext()

}
