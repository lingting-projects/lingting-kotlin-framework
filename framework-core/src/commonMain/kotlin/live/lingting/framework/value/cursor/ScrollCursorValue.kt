package live.lingting.framework.value.cursor

import live.lingting.framework.api.ScrollParams
import live.lingting.framework.api.ScrollResult
import live.lingting.framework.value.CursorValue
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2026/3/18 17:29
 */
open class ScrollCursorValue<C, V> : CursorValue<V> {

    protected var params: ScrollParams<C>?

    protected val func: (ScrollParams<C>) -> ScrollResult<V, C>

    @JvmOverloads
    constructor(params: ScrollParams<C>? = null, func: (ScrollParams<C>) -> ScrollResult<V, C>) {
        this.params = params ?: ScrollParams()
        this.func = func
    }

    @JvmOverloads
    constructor(params: ScrollParams<C>? = null, func: (ScrollParams<C>) -> List<V>) {
        this.params = params ?: ScrollParams()
        this.func = {
            val records = func(it)
            ScrollResult(records)
        }
    }

    override fun nextBatchData(): List<V>? {
        val p = params ?: return null
        val r = func(p)
        if (r.cursor == null) {
            this.params = null
        } else {
            this.params = p.copy(cursor = r.cursor)
        }
        return r.records
    }

}
