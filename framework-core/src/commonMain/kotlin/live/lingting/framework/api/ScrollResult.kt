package live.lingting.framework.api

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2024-02-02 17:54
 */
@Serializable
data class ScrollResult<V, C> @JvmOverloads constructor(
    val records: List<V> = emptyList(),
    val cursor: C? = null,
    val total: Long = records.size.toLong(),
) {

}
