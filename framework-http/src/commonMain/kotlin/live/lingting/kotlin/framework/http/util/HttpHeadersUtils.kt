package live.lingting.kotlin.framework.http.util

import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/4 16:50
 */
object HttpHeadersUtils {

    @JvmStatic
    fun HeadersBuilder?.authorization(): String? = this?.get(HttpHeaders.Authorization)

    @JvmStatic
    fun HeadersBuilder.authorization(value: String) = set(HttpHeaders.Authorization, value)

    @JvmStatic
    fun HeadersBuilder.forEachSorted(block: (String, List<String>) -> Unit) {
        names().sorted().forEach { k ->
            val vs = getAll(k)
            block(k, vs ?: listOf())
        }
    }

}
