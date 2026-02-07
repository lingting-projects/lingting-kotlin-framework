package live.lingting.kotlin.framework.value.multi

import live.lingting.kotlin.framework.collection.UnmodifiableCollection


/**
 * @author lingting 2024-09-14 11:21
 */
open class StringMultiValue : AbstractMultiValue<String, String, MutableCollection<String>> {

    constructor() : this({ mutableListOf<String>() })

    constructor(supplier: () -> MutableCollection<String>) : super(supplier)

    constructor(allowModify: Boolean) : super(allowModify, { ArrayList() })

    constructor(allowModify: Boolean, supplier: () -> MutableCollection<String>) : super(allowModify, supplier)

    override fun unmodifiable(): StringMultiValue {
        val value = StringMultiValue(false, supplier)
        value.from(this) { UnmodifiableCollection(it) }
        return value
    }

}
