package live.lingting.kotlin.framework.http

import io.ktor.http.ParametersBuilder
import io.ktor.http.encodeURLParameter
import io.ktor.util.StringValues
import live.lingting.kotlin.framework.http.util.HttpUrlUtils
import live.lingting.kotlin.framework.util.CollectionUtils
import live.lingting.kotlin.framework.util.StringUtils
import live.lingting.kotlin.framework.value.MultiValue
import live.lingting.kotlin.framework.value.multi.StringMultiValue
import kotlin.jvm.JvmOverloads


/**
 * @author lingting 2025/5/28 15:59
 */
class QueryBuilder @JvmOverloads constructor(
    private val source: StringMultiValue = StringMultiValue()
) : MultiValue<String, String, MutableCollection<String>> by source {


    constructor(map: Map<String, Any?>?) : this() {
        val values = map?.mapValues { (_, v) -> CollectionUtils.multiToList(v).mapNotNull { it?.toString() } }
        values?.run { source.appendAll(this) }
    }

    constructor(v: MultiValue<String, String, out Collection<String>>?) : this() {
        v?.run { source.appendAll(this) }
    }

    constructor(v: StringValues?) : this() {
        v?.run { source.appendAll(this) }
    }

    constructor(v: ParametersBuilder?) : this() {
        v?.run { source.appendAll(this) }
    }

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

    fun addAllAny(map: Map<String, Any?>?) {
        val values = map?.mapValues { (_, v) -> CollectionUtils.multiToList(v).mapNotNull { it?.toString() } }
        values?.run { source.appendAll(this) }
    }

    fun addAllAny(m: StringValues?) {
        m?.run { source.appendAll(this) }
    }

    fun putAllAny(map: Map<String, Any?>?) {
        val values = map?.mapValues { (_, v) -> CollectionUtils.multiToList(v).mapNotNull { it?.toString() } }
        values?.run { source.setAll(this) }
    }

    fun putAllAny(m: StringValues?) {
        m?.run { source.setAll(this) }
    }

    fun setAllAny(map: Map<String, Any?>?) = putAllAny(map)

    fun setAllAny(m: StringValues?) = putAllAny(m)

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

    fun build(): String {
        return buildString {
            forEach() { k, vs ->
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

                        val value = if (encode) HttpUrlUtils.encode(v) else v
                        append("=").append(value).append("&")
                    }
                }
            }

            if (endsWith("&")) {
                StringUtils.deleteLast(this)
            }
        }
    }

}
