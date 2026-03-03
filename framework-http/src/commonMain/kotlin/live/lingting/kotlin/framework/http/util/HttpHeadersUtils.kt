package live.lingting.kotlin.framework.http.util

import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import live.lingting.kotlin.framework.http.header.CollectionHttpHeaders
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/4 16:50
 */
object HttpHeadersUtils {

    @JvmStatic
    fun HeadersBuilder?.to(): live.lingting.kotlin.framework.http.header.HttpHeaders {
        val headers = CollectionHttpHeaders()
        this?.also {
            headers.appendAll(it)
        }
        return headers
    }

    @JvmStatic
    fun HeadersBuilder?.host(): String? = this?.get(HttpHeaders.Host)

    @JvmStatic
    fun HeadersBuilder.host(value: String) = set(HttpHeaders.Host, value)

    @JvmStatic
    fun HeadersBuilder?.authorization(): String? = this?.get(HttpHeaders.Authorization)

    @JvmStatic
    fun HeadersBuilder.authorization(value: String) = set(HttpHeaders.Authorization, value)

    @JvmStatic
    fun HeadersBuilder?.etag(): String? = this?.get(HttpHeaders.ETag)

    @JvmStatic
    fun HeadersBuilder.etag(value: String) = set(HttpHeaders.ETag, value)

    @JvmStatic
    fun HeadersBuilder.forEachSorted(block: (String, List<String>) -> Unit) {
        names().sorted().forEach { k ->
            val vs = getAll(k)
            block(k, vs ?: listOf())
        }
    }

    @JvmStatic
    fun HeadersBuilder.appendAll(source: live.lingting.kotlin.framework.http.header.HttpHeaders) {
        source.forEach { name, vs ->
            appendAll(name, vs)
        }
    }

    fun HeadersBuilder.setAll(source: live.lingting.kotlin.framework.http.header.HttpHeaders) {
        source.forEach { name, vs ->
            vs.forEachIndexed { i, v ->
                if (i == 0) {
                    set(name, v)
                } else {
                    append(name, v)
                }
            }
        }
    }

    @JvmStatic
    fun Headers.host() = get(HttpHeaders.Host)

    @JvmStatic
    fun Headers.authorization() = get(HttpHeaders.Authorization)

    @JvmStatic
    fun Headers.etag() = get(HttpHeaders.ETag)

    @JvmStatic
    fun Headers?.toHttpHeaders(): live.lingting.kotlin.framework.http.header.HttpHeaders {
        val target = CollectionHttpHeaders()
        if (this == null) return target
        forEach { k, vs ->
            target.putAll(k, vs)
        }
        return target
    }

}
