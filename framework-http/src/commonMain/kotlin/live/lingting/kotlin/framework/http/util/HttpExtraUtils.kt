package live.lingting.kotlin.framework.http.util

import io.ktor.http.ParametersBuilder
import io.ktor.util.StringValues
import live.lingting.kotlin.framework.http.util.ParametersUtils.forEach
import live.lingting.kotlin.framework.value.MultiValue

/**
 * @author lingting 2026/3/2 19:51
 */
object HttpExtraUtils {

    fun MultiValue<String, String, out Collection<String>>.appendAll(v: StringValues) {
        v.forEach { name, vs ->
            appendAll(name, vs)
        }
    }

    fun MultiValue<String, String, out Collection<String>>.appendAll(v: ParametersBuilder) {
        v.forEach { name, vs ->
            appendAll(name, vs)
        }
    }

    fun MultiValue<String, String, out Collection<String>>.setAll(v: StringValues) {
        v.forEach { name, vs ->
            setAll(name, vs)
        }
    }

    fun MultiValue<String, String, out Collection<String>>.setAll(v: ParametersBuilder) {
        v.forEach { name, vs ->
            setAll(name, vs)
        }
    }

}
