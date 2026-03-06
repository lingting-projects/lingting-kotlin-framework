package live.lingting.framework.http.util

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ParametersBuilder
import io.ktor.util.StringValues
import kotlinx.coroutines.cancel
import live.lingting.framework.http.util.ParametersUtils.forEach
import live.lingting.framework.value.MultiValue

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

    suspend fun <T> HttpResponse.use(func: suspend (HttpResponse) -> T): T {
        try {
            return func(this)
        } finally {
            try {
                this.cancel()
            } catch (_: Throwable) {
                //
            }
        }
    }

    suspend fun <T> HttpResponse.convert(func: suspend (String) -> T): T {
        return use {
            val str = it.bodyAsText()
            func(str)
        }
    }

}
