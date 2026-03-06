package live.lingting.framework.value

import kotlinx.coroutines.flow.asFlow

/**
 * @author lingting 2026/2/26 17:31
 */
abstract class CursorValue<T> : Iterator<T> {

    protected abstract fun nextBatchData(): List<T>?

    protected val source = sequence {
        while (true) {
            val batch = nextBatchData()
            if (batch.isNullOrEmpty()) break
            yieldAll(batch)
        }
    }

    protected val iterator = source.iterator()

    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    override fun next(): T {
        return iterator.next()
    }

    fun asSequence(): Sequence<T> = source

    fun asFlow() = source.asFlow()

    fun toList(): List<T> = source.toList()

}
