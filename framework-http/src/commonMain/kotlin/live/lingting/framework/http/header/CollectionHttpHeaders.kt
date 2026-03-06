package live.lingting.framework.http.header

import kotlin.jvm.JvmOverloads


/**
 * @author lingting 2024-09-12 23:41
 */
open class CollectionHttpHeaders : live.lingting.framework.http.header.AbstractHttpHeaders,
    live.lingting.framework.http.header.HttpHeaders {

    @JvmOverloads
    constructor(allowModify: Boolean = true, supplier: () -> MutableCollection<String> = { mutableListOf() })
            : super(allowModify, supplier)

}
