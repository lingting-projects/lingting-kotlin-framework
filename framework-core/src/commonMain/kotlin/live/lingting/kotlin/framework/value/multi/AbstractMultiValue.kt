package live.lingting.kotlin.framework.value.multi

import live.lingting.kotlin.framework.value.MultiValue

/**
 * @author lingting 2024-09-05 20:33
 */
abstract class AbstractMultiValue<K, V, C : MutableCollection<V>> protected constructor(
    protected val allowModify: Boolean,
    protected val supplier: () -> C
) : MultiValue<K, V, C> {

    protected constructor(supplier: () -> C) : this(true, supplier)

    protected val map: MutableMap<K, C> = LinkedHashMap()

    protected open fun convert(key: K): K {
        return key
    }

    protected fun absent(key: K): C {
        val k = convert(key)
        if (!allowModify && !hasKey(k)) {
            throw UnsupportedOperationException()
        }
        return map.getOrPut(k, supplier)
    }

    override fun ifAbsent(key: K) {
        absent(key)
    }

    // region fill
    override fun add(key: K) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        absent(key)
    }

    override fun add(key: K, value: V) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        absent(key).add(value)
    }

    override fun addAll(key: K, values: Iterable<V>) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        val c = absent(key)
        values.forEach { c.add(it) }
    }

    override fun addAll(map: Map<K, Iterable<V>>) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        map.forEach { (key: K, values) -> this.addAll(key, values) }
    }

    override fun addAll(value: MultiValue<K, V, out Collection<V>>) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        value.forEach { key, values -> this.addAll(key, values) }
    }

    override fun put(key: K, value: V) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        putAll(key, listOf(value))
    }

    override fun putAll(key: K, values: Iterable<V>) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        val key = convert(key)
        val c = supplier()
        values.forEach { c.add(it) }
        map[key] = c
    }

    override fun putAll(map: Map<K, Iterable<V>>) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        map.forEach { (key: K, values) -> this.putAll(key, values) }
    }

    override fun putAll(value: MultiValue<K, V, out Collection<V>>) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        value.forEach { key, values -> this.putAll(key, values) }
    }

    override fun replace(oldKey: K, newKey: K) {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        val c = map.remove(oldKey)
        if (c != null) {
            map[newKey] = c
        }
    }

    protected fun from(value: MultiValue<K, V, out Collection<V>>) {
        from(value) { vs ->
            val c = supplier()
            c.addAll(vs)
            c
        }
    }

    protected fun <S : Collection<V>> from(value: MultiValue<K, V, S>, function: (S) -> C) {
        value.forEach { k, vs ->
            val rk = convert(k)
            val rv = function(vs)
            map[rk] = rv
        }
    }

    // endregion

    // region get

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun isEmpty(key: K): Boolean {
        return isEmpty() || !hasKey(key) || absent(key).isEmpty()
    }

    override fun size(): Int {
        return map.size
    }

    override fun hasKey(key: K): Boolean {
        val k = convert(key)
        return map.containsKey(k)
    }

    override fun get(key: K): C {
        if (!allowModify && !hasKey(key)) {
            return supplier()
        }
        return absent(key)
    }

    override fun iterator(key: K): Iterator<V> {
        return get(key).iterator()
    }

    override fun first(key: K): V? {
        val collection = get(key)
        if (collection.isEmpty()) {
            return null
        }
        return collection.iterator().next()
    }

    override fun keys(): Set<K> {
        return map.keys.map { convert(it) }.toSet()
    }

    override fun values(): Collection<C> {
        return map.values
    }

    override fun map(): Map<K, C> {
        val hashMap: MutableMap<K, C> = HashMap()
        map.forEach { (ok, vs) ->
            val k = convert(ok)
            val c = supplier()
            c.addAll(vs)
            hashMap[k] = c
        }
        return hashMap
    }

    override fun entries(): List<MultiValue.Entry<K, V, C>> {
        return map.entries.map { MultiValue.Entry(convert(it.key), it.value) }
    }

    override fun unmodifiable(): MultiValue<K, V, out Collection<V>> {
        return UnmodifiableMultiValue(this)
    }

    // endregion

    // region remove
    override fun clear() {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        map.clear()
    }

    override fun remove(key: K): C? {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        return map.remove(key)
    }

    override fun remove(key: K, value: V): Boolean {
        if (!allowModify) {
            throw UnsupportedOperationException()
        }
        if (!hasKey(key)) {
            return false
        }
        val absent = absent(key)
        return absent.remove(value)
    }

    // endregion

}
