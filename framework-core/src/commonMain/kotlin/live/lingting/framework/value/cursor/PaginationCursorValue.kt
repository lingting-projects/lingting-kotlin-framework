package live.lingting.framework.value.cursor

import live.lingting.framework.api.PaginationParams
import live.lingting.framework.api.PaginationResult
import live.lingting.framework.value.CursorValue
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2026/3/18 17:29
 */
open class PaginationCursorValue<V> : CursorValue<V> {

    protected var params: PaginationParams?

    protected val func: (PaginationParams) -> PaginationResult<V>

    @JvmOverloads
    constructor(params: PaginationParams? = null, func: (PaginationParams) -> PaginationResult<V>) {
        this.params = params ?: PaginationParams()
        this.func = func
    }

    override fun nextBatchData(): List<V>? {
        val p = params ?: return null
        val r = func(p)
        if (r.records.size < p.size) {
            this.params = null
        } else {
            this.params = p.copy(page = p.page + 1)
        }
        return r.records
    }

}
