package live.lingting.framework.value.multi

import live.lingting.framework.collection.UnmodifiableCollection


/**
 * @author lingting 2024-09-05 21:07
 */
class UnmodifiableMultiValue<K, V>(value: AbstractMultiValue<K, V, *>) :
    AbstractMultiValue<K, V, MutableCollection<V>>(false, { mutableListOf<V>() }) {

    init {
        from(value) { UnmodifiableCollection(it) }
    }

}
