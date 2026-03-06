package live.lingting.framework.aws.signer

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HeadersBuilder
import io.ktor.http.URLBuilder
import kotlinx.datetime.LocalDateTime
import live.lingting.framework.http.util.HttpHeadersUtils.authorization
import live.lingting.framework.http.util.HttpHeadersUtils.setAll
import live.lingting.framework.http.util.ParametersUtils.appendAll
import live.lingting.framework.time.DateTime
import live.lingting.framework.util.DurationUtils.between
import kotlin.time.Duration


/**
 * @author lingting 2026/2/4 16:16
 */
abstract class AwsSigner<S : AwsSigner<S, R>, R : AwsSigner.Signed<S, R>>(
    open val ak: String
) {

    protected abstract val bodyPayload: String

    open fun signed(): R = signed(DateTime.current())

    /**
     * header 签名
     * @param time: 签名时间
     */
    open fun signed(time: LocalDateTime): R = signed(time, bodyPayload)

    abstract fun signed(time: LocalDateTime, bodyPayload: String): R

    /**
     * url 签名
     * @param time: 签名时间
     * @param expire: 过期时间
     */
    open fun signed(time: LocalDateTime, expire: LocalDateTime): R = signed(time, expire, bodyPayload)

    open fun signed(time: LocalDateTime, expire: LocalDateTime, bodyPayload: String): R {
        val duration = Duration.between(time, expire)
        return signed(time, duration, bodyPayload)
    }

    open fun signed(duration: Duration): R =
        signed(DateTime.current(), duration)

    /**
     * url 签名
     * @param time: 签名时间
     * @param duration: 有效时长
     */
    open fun signed(time: LocalDateTime, duration: Duration): R = signed(time, duration, bodyPayload)

    abstract fun signed(time: LocalDateTime, duration: Duration, bodyPayload: String): R

    open class Signed<S : AwsSigner<S, R>, R : Signed<S, R>>(
        open val signer: S,
        open val headers: live.lingting.framework.http.header.HttpHeaders,
        open val params: live.lingting.framework.value.multi.StringMultiValue?,
        open val bodyPayload: String,
        open val source: String,
        open val sign: String,
        open val authorization: String,
    ) {

        open fun replace(headers: live.lingting.framework.http.header.HttpHeaders?) {
            if (headers == null) {
                return
            }
            headers.clear()
            headers.setAll(this.headers)
            if (authorization.isNotBlank()) {
                headers.authorization(authorization)
            }
        }

        open fun replace(urlBuilder: URLBuilder? = null) {
            if (params == null || urlBuilder == null) {
                return
            }
            val up = urlBuilder.parameters
            up.clear()
            params?.run { up.appendAll(this) }
        }

        open fun replace(
            headers: live.lingting.framework.http.header.HttpHeaders? = null,
            urlBuilder: URLBuilder? = null
        ) {
            replace(headers)
            replace(urlBuilder)
        }

        open fun replace(headers: HeadersBuilder? = null) {
            if (headers == null) {
                return
            }
            headers.clear()
            headers.setAll(this.headers)
            if (authorization.isNotBlank()) {
                headers.authorization(authorization)
            }
        }

        open fun replace(builder: HttpRequestBuilder? = null) {
            if (builder == null) {
                return
            }
            replace(builder.url)
            replace(builder.headers)
        }

    }

}
