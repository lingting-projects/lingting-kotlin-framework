package live.lingting.framework.api

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2024-02-02 17:53
 */
@Serializable
data class PaginationResult<T> @JvmOverloads constructor(
    val records: List<T> = emptyList(),
    val total: Long = 0,
) {

    fun <V : Any> map(function: (T) -> V): PaginationResult<V> {
        return PaginationResult(records.map { function(it) }, total)
    }

    fun forEach(consumer: (T) -> Unit) {
        records.forEach(consumer)
    }

}
