package live.lingting.kotlin.framework.util

import live.lingting.kotlin.framework.util.ArrayUtils.isArray
import live.lingting.kotlin.framework.util.ArrayUtils.isEmpty
import live.lingting.kotlin.framework.value.MultiValue
import kotlin.jvm.JvmStatic
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * @author lingting 2024-01-26 15:47
 */
object ValueUtils {

    /**
     * 当前对象是否非null，且不为空
     * @param value 值
     * @return boolean 不为空返回true
     */
    @JvmStatic
    fun isPresent(value: Any?): Boolean {
        if (value == null) {
            return false
        }
        if (value is CharSequence) {
            return StringUtils.hasText(value)
        }
        if (value is Collection<*>) {
            return value.isNotEmpty()
        }
        if (value is Map<*, *>) {
            return value.isNotEmpty()
        }
        if (value.isArray()) {
            return !value.isEmpty()
        }
        return true
    }

    @OptIn(ExperimentalUuidApi::class)
    @JvmStatic
    fun uuid(): String {
        val uuid = Uuid.random()
        return uuid.toHexDashString()
    }

    @OptIn(ExperimentalUuidApi::class)
    @JvmStatic
    fun simpleUuid(): String {
        val uuid = Uuid.random()
        return uuid.toHexString()
    }

    @JvmStatic
    fun <K : Comparable<K>, V, C : Collection<V>> MultiValue<K, V, C>.forEachSorted(consumer: (K, C) -> Unit) {
        keys().sorted().forEach { key -> consumer(key, get(key)) }
    }

    @JvmStatic
    fun <K : Comparable<K>, V, C : Collection<V>> MultiValue<K, V, C>.eachSorted(consumer: (K, V) -> Unit) {
        forEachSorted { k, c -> c.forEach { v -> consumer(k, v) } }
    }

}
