package live.lingting.kotlin.framework.http.api

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.takeFrom
import io.ktor.util.appendAll
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.Closeable
import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.io.buffered
import live.lingting.kotlin.framework.http.HttpClient.default
import kotlin.jvm.JvmField

/**
 * @author lingting 2026/1/31 23:24
 */
abstract class ApiClient<R : ApiRequest>(@JvmField protected val host: String) {

    companion object {

        @JvmField
        var defaultClient: HttpClient = default()

    }

    @JvmField
    protected var client = defaultClient

    protected open val hostUrl = Url(host)

    /**
     * 检查返回值是否符合预期, 不符合则抛出异常
     */
    abstract suspend fun checkout(r: R, response: HttpResponse)

    protected open fun onCall(r: R) {
        //
    }

    protected open fun onHeadersAfter(r: R, headers: HeadersBuilder) {}

    protected open fun buildUrl(r: R, builder: URLBuilder) {
        builder.takeFrom(hostUrl)
        val path = r.path()
        builder.appendPathSegments(path.split("/"))
        builder.parameters.appendAll(r.params)
    }

    protected open fun buildHeaders(r: R, headers: HeadersBuilder) {
        if (r.headers.isEmpty()) {
            return
        }
        headers.appendAll(r.headers)
    }

    protected suspend fun call(r: R): HttpResponse {
        r.onBuildBefore()
        onCall(r)
        val builder = HttpRequestBuilder()

        r.onUrlUrlBefore()
        buildUrl(r, builder.url)
        builder.method = r.method()
        val contentType = r.contentType()
        if (contentType != null) {
            builder.headers[HttpHeaders.ContentType] = contentType
        }
        buildHeaders(r, builder.headers)
        onHeadersAfter(r, builder.headers)
        return call(r, builder)
    }

    protected open suspend fun call(r: R, builder: HttpRequestBuilder): HttpResponse {
        val body = r.body()
        var source = body.source()
        when (source) {
            is ByteArray -> {
                builder.body = ByteReadChannel(source)
            }

            is Source -> {
                builder.body = ByteReadChannel(source)
            }

            is RawSource -> {
                val buffered = source.buffered()
                source = buffered
                builder.body = ByteReadChannel(buffered)
            }

            else -> {
                builder.body = body
            }
        }

        val response = try {
            client.request(builder)
        } finally {
            close(source)
        }
        checkout(r, response)
        return response
    }

    protected open fun close(e: Any?) {
        try {
            if (e == null) {
                return
            }
            if (e is AutoCloseable) {
                e.close()
            } else if (e is Closeable) {
                e.close()
            }
        } catch (e: Exception) {
            //
        }
    }

}
