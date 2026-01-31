package live.lingting.kotlin.framework.util

import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.math.max
import kotlin.math.min

/**
 * @author lingting
 */
@Suppress("UNCHECKED_CAST")
object ArrayUtils {

    const val NOT_FOUNT: Int = -1

    @JvmStatic
    fun <T : Any> T?.isArray(): Boolean {
        return when (this) {
            null -> false
            // 对象数组
            is Array<*> -> true

            // 基本类型数组
            is IntArray -> true
            is LongArray -> true
            is ByteArray -> true
            is DoubleArray -> true
            is FloatArray -> true
            is ShortArray -> true
            is BooleanArray -> true
            is CharArray -> true

            else -> false
        }
    }

    @JvmStatic
    fun arrayToList(obj: Any?): List<Any?> {
        return when (obj) {
            null -> emptyList()
            // 对象数组
            is Array<*> -> obj.toList()

            // 基本类型数组
            is IntArray -> obj.toList()
            is LongArray -> obj.toList()
            is ByteArray -> obj.toList()
            is DoubleArray -> obj.toList()
            is FloatArray -> obj.toList()
            is ShortArray -> obj.toList()
            is BooleanArray -> obj.toList()
            is CharArray -> obj.toList()

            else -> emptyList()
        }
    }

    /**
     * 数组是否为空
     * @param obj 对象
     * @return true表示为空, 如果对象不为数组, 返回false
     */
    @JvmStatic
    fun <T : Any> T?.isEmpty(): Boolean {
        return when (this) {
            null -> true
            // 对象数组
            is Array<*> -> this.size == 0

            // 基本类型数组
            is IntArray -> this.size == 0
            is LongArray -> this.size == 0
            is ByteArray -> this.size == 0
            is DoubleArray -> this.size == 0
            is FloatArray -> this.size == 0
            is ShortArray -> this.size == 0
            is BooleanArray -> this.size == 0
            is CharArray -> this.size == 0

            // 集合或 Map 顺便支持
            is Collection<*> -> this.isEmpty()
            is Map<*, *> -> this.isEmpty()

            else -> false
        }
    }

    @JvmStatic
    fun <T : Any> Array<T>?.isEmpty(): Boolean {
        return isNullOrEmpty()
    }

    @JvmStatic
    fun <T : Any> Array<T>.indexOf(value: T): Int {
        return indexOf(value) { a, b -> a == b }
    }

    @JvmStatic
    fun <T : Any> Array<T>.indexOf(value: T, predicate: (a: T, b: T) -> Boolean): Int {
        if (!isEmpty()) {
            for (i in indices) {
                val t = this[i]
                if (predicate(t, value)) {
                    return i
                }
            }
        }
        return NOT_FOUNT
    }

    @JvmStatic
    fun <T : Any> Array<T>.contains(value: T): Boolean {
        return indexOf(value) > NOT_FOUNT
    }

    @JvmStatic
    fun Array<String>.containsIgnoreCase(value: String): Boolean {
        return indexOf(value) { s, t ->
            if (s == t) {
                return@indexOf true
            }
            s.equals(t, ignoreCase = true)
        } > NOT_FOUNT
    }

    @JvmStatic
    fun arrayEquals(a: Any?, b: Any?): Boolean {
        return arrayEquals(0, a, 0, b)
    }

    /**
     * 数组对比, 从pos开始每一项都要一致
     * @param len len 为null或者小于1 表示两个数组从pos开始必须完全一致. 否则表示从pos开始 指定数量元素一致
     */
    @JvmStatic
    @JvmOverloads
    fun arrayEquals(aPos: Int, a: Any?, bPos: Int, b: Any?, len: Int? = null): Boolean {
        if (!a.isArray() || !b.isArray()) {
            return false
        }
        val al = arrayToList(a)
        val bl = arrayToList(b)

        if (al.isEmpty() && bl.isEmpty()) {
            return true
        }

        var i = 0
        while (true) {
            val ai = aPos + i
            val bi = bPos + i
            val ao = al.size <= ai
            val bo = bl.size <= bi
            // 越界
            if (ao || bo) {
                // 如果同时越界, 则相等, 否则不等
                return ao && bo
            }

            val va = al[ai]
            val vb = bl[bi]

            if (va != vb) {
                return false
            }

            i += 1
            if (len != null && len > 0 && i >= len) {
                return true
            }
        }
    }

    @JvmStatic
    fun <T : Any> Array<T>?.sub(start: Int): Array<T>? {
        if (this == null) {
            return null
        }
        return sub(start, size)
    }

    /**
     * 截取数组
     * @param array 数组
     * @param start 左闭
     * @param end   右开
     */
    @JvmStatic
    fun <T : Any> Array<T>.sub(start: Int, end: Int): Array<T> {
        if (isEmpty()) {
            return copyOfRange(0, 0)
        }

        val fromIndex: Int = max(0, start)
        val toIndex: Int = min(size, end)

        if (fromIndex >= toIndex) {
            return copyOfRange(0, 0)
        }

        return this.copyOfRange(fromIndex, toIndex)
    }

}
