package live.lingting.kotlin.framework.http.header

import kotlin.jvm.JvmOverloads


/**
 * @author lingting 2024-09-12 23:41
 */
open class CollectionHttpHeaders : AbstractHttpHeaders, HttpHeaders {

    @JvmOverloads
    constructor(allowModify: Boolean = true, supplier: () -> MutableCollection<String> = { mutableListOf() })
            : super(allowModify, supplier)

}
