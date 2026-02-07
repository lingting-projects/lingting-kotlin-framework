package live.lingting.kotlin.framework.value

/**
 * @author lingting 2024-09-05 21:17
 */
interface MultiValue<K, V, C : Collection<V>> {

    // region fill
    /**
     * 为指定key创建一个槽位(如果不存在)
     */
    fun ifAbsent(key: K)

    fun add(key: K)

    fun add(key: K, value: V)

    fun addAll(key: K, values: Iterable<V>)

    fun addAll(map: Map<K, Iterable<V>>)

    fun addAll(value: MultiValue<K, V, out Collection<V>>)

    fun put(key: K, value: V)

    fun putAll(key: K, values: Iterable<V>)

    fun putAll(map: Map<K, Iterable<V>>)

    fun putAll(value: MultiValue<K, V, out Collection<V>>)

    fun replace(oldKey: K, newKey: K)

    // endregion

    // region ktor 函数适配

    fun append(key: K) = add(key)

    fun append(key: K, value: V) = add(key, value)

    fun appendAll(key: K, values: Iterable<V>) = addAll(key, values)

    fun appendAll(map: Map<K, Iterable<V>>) = addAll(map)

    fun appendAll(value: MultiValue<K, V, out Collection<V>>) = addAll(value)

    operator fun set(key: K, value: V) = put(key, value)

    fun setAll(key: K, values: Iterable<V>) = putAll(key, values)

    fun setAll(map: Map<K, Iterable<V>>) = putAll(map)

    fun setAll(value: MultiValue<K, V, out Collection<V>>) = putAll(value)

    // endregion

    // region get

    fun isEmpty(): Boolean

    fun isEmpty(key: K): Boolean

    fun size(): Int

    fun hasKey(key: K): Boolean

    operator fun get(key: K): C

    fun iterator(key: K): Iterator<V>

    fun first(key: K): V?

    fun first(key: K, defaultValue: V): V {
        val v = first(key)
        return v ?: defaultValue
    }

    fun keys(): Set<K>

    fun values(): Collection<C>

    fun map(): Map<K, C>

    fun entries(): List<Entry<K, V, C>>

    fun unmodifiable(): MultiValue<K, V, out Collection<V>>

    // endregion

    // region remove
    fun clear()

    fun remove(key: K): C?

    fun remove(key: K, value: V): Boolean

    // endregion

    // region function

    fun forEach(consumer: (K, C) -> Unit) {
        entries().forEach { consumer(it.key, it.value) }
    }

    fun each(consumer: (K, V) -> Unit) {
        forEach { k, c -> c.forEach { v -> consumer(k, v) } }
    }

    fun <K : Comparable<K>, V, C : Collection<V>> MultiValue<K, V, C>.forEachSorted(consumer: (K, C) -> Unit) {
        keys().sorted().forEach { key -> consumer(key, get(key)) }
    }

    fun forEachSorted(consumer: (K, C) -> Unit, comparator: Comparator<K>) {
        keys().sortedWith(comparator).forEach { key -> consumer(key, get(key)) }
    }

    fun <K : Comparable<K>, V, C : Collection<V>> MultiValue<K, V, C>.eachSorted(consumer: (K, V) -> Unit) {
        forEachSorted { k, c -> c.forEach { v -> consumer(k, v) } }
    }

    fun eachSorted(consumer: (K, V) -> Unit, comparator: Comparator<K>) {
        forEachSorted({ k, c -> c.forEach { v -> consumer(k, v) } }, comparator)
    }

    // endregion

    class Entry<K, V, C : Collection<V>>(
        override val key: K,
        override val value: C
    ) : Map.Entry<K, C> {

    }

}
