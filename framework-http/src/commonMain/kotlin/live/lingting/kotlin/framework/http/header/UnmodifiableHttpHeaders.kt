package live.lingting.kotlin.framework.http.header

import live.lingting.kotlin.framework.collection.UnmodifiableCollection
import live.lingting.kotlin.framework.value.multi.AbstractMultiValue

/**
 * @author lingting 2024-09-12 23:45
 */
open class UnmodifiableHttpHeaders(value: AbstractMultiValue<String, String, *>) :
    AbstractHttpHeaders(false, { mutableListOf() }), HttpHeaders {

    init {
        from(value) { UnmodifiableCollection(it) }
    }

    override fun unmodifiable(): UnmodifiableHttpHeaders {
        return this
    }

}
