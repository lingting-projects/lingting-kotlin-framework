package live.lingting.kotlin.framework.http.util

import io.ktor.http.ParametersBuilder
import io.ktor.util.StringValues
import io.ktor.util.appendAll
import live.lingting.kotlin.framework.value.MultiValue
import live.lingting.kotlin.framework.value.multi.StringMultiValue
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/4 16:53
 */
object ParametersUtils {

    @JvmStatic
    fun ParametersBuilder?.forEach(block: (String, List<String>) -> Unit) {
        this?.names()?.forEach { k ->
            val vs = getAll(k)
            block(k, vs ?: listOf())
        }
    }

    @JvmStatic
    fun ParametersBuilder.setAll(source: Map<String, List<String>>) {
        source.forEach { (k, vs) ->
            remove(k)
            appendAll(k, vs)
        }
    }

    @JvmStatic
    fun ParametersBuilder.setAll(source: StringValues) {
        source.forEach { k, vs ->
            remove(k)
            appendAll(k, vs)
        }
    }

    @JvmStatic
    fun ParametersBuilder.copy(): ParametersBuilder {
        return ParametersBuilder().also { it.appendAll(this) }
    }

    @JvmStatic
    fun ParametersBuilder.to(): StringMultiValue {
        val value = StringMultiValue()
        forEach { k, vs ->
            value.appendAll(k, vs)
        }
        return value
    }

    @JvmStatic
    fun <C : Collection<String>> ParametersBuilder.appendAll(source: MultiValue<String, String, C>) {
        source.forEach { k, vs ->
            appendAll(k, vs)
        }
    }

}
