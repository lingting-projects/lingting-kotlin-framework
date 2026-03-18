package live.lingting.framework.api

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2024-02-02 17:53
 */
@Serializable
data class PaginationParams @JvmOverloads constructor(
    val page: Long = 1,
    val size: Long = 10,
    val sorts: List<Sort> = emptyList(),
) {

    /**
     * 数据起始索引
     */
    fun start(): Long {
        return (page - 1) * size
    }

    data class Sort @JvmOverloads constructor(

        /**
         * 排序字段
         */
        var field: String,

        /**
         * 是否倒序
         */
        var desc: Boolean = true,
    ) {

    }

}
