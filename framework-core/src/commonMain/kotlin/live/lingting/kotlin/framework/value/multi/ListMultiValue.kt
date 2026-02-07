package live.lingting.kotlin.framework.value.multi


/**
 * @author lingting 2024-09-05 20:28
 */
class ListMultiValue<K, V> : AbstractMultiValue<K, V, MutableList<V>> {

    constructor() : super({ ArrayList<V>() })

    constructor(supplier: () -> MutableList<V>) : super(supplier)

}
