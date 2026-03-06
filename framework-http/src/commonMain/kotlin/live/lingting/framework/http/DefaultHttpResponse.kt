package live.lingting.framework.http

import io.ktor.client.call.HttpClientCall
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/13 14:34
 */
class DefaultHttpResponse private constructor(
    private val _call: HttpClientCall?,
    private val _status: HttpStatusCode?,
    private val _version: HttpProtocolVersion?,
    private val _requestTime: GMTDate?,
    private val _responseTime: GMTDate?,
    private val _headers: Headers?,
    private val _coroutineContext: CoroutineContext?,
    @InternalAPI private val _rawContent: ByteReadChannel?
) : HttpResponse() {

    companion object {

        @JvmStatic
        fun build(block: Builder.() -> Unit) = builder().apply(block).build()

        @JvmStatic
        fun builder() = Builder()

    }

    // 统一处理 Null 检查
    private fun <T> ensure(value: T?): T {
        return value
            ?: throw UnsupportedOperationException("This property is not implemented/provided in this response instance.")
    }

    override val call: HttpClientCall get() = ensure(_call)
    override val status: HttpStatusCode get() = ensure(_status)
    override val version: HttpProtocolVersion get() = ensure(_version)
    override val requestTime: GMTDate get() = ensure(_requestTime)
    override val responseTime: GMTDate get() = ensure(_responseTime)
    override val headers: Headers get() = ensure(_headers)
    override val coroutineContext: CoroutineContext get() = ensure(_coroutineContext)

    @InternalAPI
    override val rawContent: ByteReadChannel get() = ensure(_rawContent)

    /**
     * 构建类
     */
    class Builder {

        @JvmField
        var call: HttpClientCall? = null

        @JvmField
        var status: HttpStatusCode? = HttpStatusCode.OK

        @JvmField
        var version: HttpProtocolVersion? = HttpProtocolVersion.HTTP_1_1

        @JvmField
        var requestTime: GMTDate? = GMTDate()

        @JvmField
        var responseTime: GMTDate? = GMTDate()

        @JvmField
        var headers: Headers? = Headers.Empty

        @JvmField
        var coroutineContext: CoroutineContext? = null

        @JvmField
        var rawContent: ByteReadChannel? = null

        fun body(bytes: ByteArray): Builder {
            this.rawContent = ByteReadChannel(bytes)
            return this
        }

        fun body(text: String, charset: Charset = Charsets.UTF_8): Builder {
            return body(text.toByteArray(charset))
        }

        fun build() = DefaultHttpResponse(
            call, status, version, requestTime, responseTime, headers, coroutineContext, rawContent
        )
    }

}
