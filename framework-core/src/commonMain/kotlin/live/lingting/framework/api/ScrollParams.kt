package live.lingting.framework.api

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2024-02-02 17:54
 */
@Serializable
data class ScrollParams<C> @JvmOverloads constructor(
    var size: Long = 10,
    var cursor: C? = null,
) {

}
