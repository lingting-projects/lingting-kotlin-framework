package live.lingting.framework.value.cursor

import live.lingting.framework.value.CursorValue

/**
 * @author lingting 2026/2/26 17:38
 */
class SimpleCursorValue<T>(private val supplier: () -> List<T>?) : CursorValue<T>() {

    override fun nextBatchData(): List<T>? {
        return supplier()
    }

}
