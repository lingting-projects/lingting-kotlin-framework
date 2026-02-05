package live.lingting.kotlin.framework.http

import io.ktor.http.ParametersBuilder
import io.ktor.http.encodeURLParameter
import io.ktor.util.StringValues
import io.ktor.util.appendAll
import live.lingting.kotlin.framework.http.util.ParametersUtils.copy
import live.lingting.kotlin.framework.http.util.ParametersUtils.setAll
import live.lingting.kotlin.framework.util.CollectionUtils
import live.lingting.kotlin.framework.util.StringUtils


/**
 * @author lingting 2025/5/28 15:59
 */
class QueryBuilder {

    constructor()

    constructor(s: Map<String, Any?>?) {
        val values = s?.mapValues { (_, v) -> CollectionUtils.multiToList(v).mapNotNull { it?.toString() } }
        values?.run { source.appendAll(this) }
    }

    constructor(s: StringValues?) {
        s?.run { source.appendAll(this) }
    }

    constructor(s: ParametersBuilder?) {
        s?.run { source.appendAll(this) }
    }

    private val source = ParametersBuilder()

    fun source() = ParametersBuilder().apply { appendAll(source) }

    var encode = true

    var sort = false

    /**
     * 自定义排序
     */
    var comparator: Comparator<String>? = null

    /**
     * 空值名称添加 =
     */
    var emptyValueEqual = false

    /**
     * 是否在名称后面添加索引作为后缀
     */
    var indexSuffix = false

    /**
     * 分隔符
     */
    var indexSuffixSeparation = "."

    /**
     * 是否给空值添加后缀
     */
    var indexSuffixEmpty = false

    /**
     * 是否给所有的名称均添加后缀
     */
    var indexSuffixAll = true

    /**
     * 当 indexSuffixAll=false 时. 仅在这个set中找到的名称进去后缀添加
     */
    var indexMatchNames = setOf<String>()

    /**
     * index 起始值
     */
    var indexStart = 1

    fun add(name: String, v: String) = source.append(name, v)

    fun addAll(map: Map<String, Any?>?) {
        val values = map?.mapValues { (_, v) -> CollectionUtils.multiToList(v).mapNotNull { it?.toString() } }
        values?.run { source.appendAll(this) }
    }

    fun addAll(m: StringValues?) {
        m?.run { source.appendAll(this) }
    }

    fun put(name: String, v: String) = source.set(name, v)

    fun putAll(map: Map<String, Any?>?) {
        val values = map?.mapValues { (_, v) -> CollectionUtils.multiToList(v).mapNotNull { it?.toString() } }
        values?.run { source.setAll(this) }
    }

    fun putAll(m: StringValues?) {
        m?.run { source.setAll(this) }
    }

    fun build(): String {
        if (source.isEmpty()) {
            return ""
        }

        val copy = source.copy()
        return buildString {
            val keys = sort(copy.names())
            for (k in keys) {
                val vs = sort(copy.getAll(k))
                val name = if (encode) k.encodeURLParameter() else k

                if (vs.isEmpty()) {
                    append(name)
                    if (indexSuffix && indexSuffixEmpty) {
                        append(indexSuffixSeparation).append(indexStart)
                    }
                    if (emptyValueEqual) {
                        append("=")
                    }
                    append("&")
                } else {
                    vs.forEachIndexed { i, v ->
                        append(name)
                        if (indexSuffix && indexSuffixAll || indexMatchNames.contains(k)) {
                            append(indexSuffixSeparation).append(indexStart + i)
                        }

                        val value = if (encode) v.encodeURLParameter() else v
                        append("=").append(value).append("&")
                    }
                }
            }

            if (endsWith("&")) {
                StringUtils.deleteLast(this)
            }
        }
    }

    private fun sort(c: Collection<String>?): List<String> {
        if (c == null) {
            return emptyList()
        }
        if (!sort) {
            return c.toList()
        }
        val r = comparator ?: return c.sorted()
        return c.sortedWith(r)
    }

}
