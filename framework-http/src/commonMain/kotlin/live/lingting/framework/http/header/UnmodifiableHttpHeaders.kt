package live.lingting.framework.http.header

import live.lingting.framework.collection.UnmodifiableCollection
import live.lingting.framework.value.multi.AbstractMultiValue

/**
 * @author lingting 2024-09-12 23:45
 */
open class UnmodifiableHttpHeaders(value: AbstractMultiValue<String, String, *>) :
    live.lingting.framework.http.header.AbstractHttpHeaders(false, { mutableListOf() }),
    live.lingting.framework.http.header.HttpHeaders {

    init {
        from(value) { UnmodifiableCollection(it) }
    }

    override fun unmodifiable(): live.lingting.framework.http.header.UnmodifiableHttpHeaders {
        return this
    }

}
