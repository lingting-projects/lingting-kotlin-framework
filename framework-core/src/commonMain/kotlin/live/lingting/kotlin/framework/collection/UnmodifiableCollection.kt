package live.lingting.kotlin.framework.collection

/**
 * @author lingting 2026/2/6 16:24
 */
class UnmodifiableCollection<T>(private val source: Collection<T>) : MutableCollection<T> {

    override fun iterator(): MutableIterator<T> = UnmodifiableIterator(source.iterator())

    override fun add(element: T): Boolean = throw UnsupportedOperationException()

    override fun remove(element: T): Boolean = throw UnsupportedOperationException()

    override fun addAll(elements: Collection<T>): Boolean = throw UnsupportedOperationException()

    override fun removeAll(elements: Collection<T>): Boolean = throw UnsupportedOperationException()

    override fun retainAll(elements: Collection<T>): Boolean = throw UnsupportedOperationException()

    override fun clear() = throw UnsupportedOperationException()

    override val size: Int
        get() = source.size

    override fun isEmpty(): Boolean = source.isEmpty()

    override fun contains(element: T): Boolean = source.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = source.containsAll(elements)

}
