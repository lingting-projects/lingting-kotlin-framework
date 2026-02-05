package live.lingting.kotlin.framework.aws.signer

import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.ParametersBuilder
import io.ktor.http.URLBuilder
import io.ktor.util.appendAll
import kotlinx.datetime.LocalDateTime
import live.lingting.kotlin.framework.http.util.HttpHeadersUtils.authorization
import live.lingting.kotlin.framework.http.util.ParametersUtils.forEach
import live.lingting.kotlin.framework.time.DateTime
import live.lingting.kotlin.framework.util.DurationUtils.between
import kotlin.time.Duration


/**
 * @author lingting 2026/2/4 16:16
 */
abstract class AwsSigner<S : AwsSigner<S, R>, R : AwsSigner.Signed<S, R>>(open val ak: String) {

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

    open fun signed(duration: Duration): R = signed(DateTime.current(), duration)

    /**
     * url 签名
     * @param time: 签名时间
     * @param duration: 有效时长
     */
    open fun signed(time: LocalDateTime, duration: Duration): R = signed(time, duration, bodyPayload)

    abstract fun signed(time: LocalDateTime, duration: Duration, bodyPayload: String): R

    open class Signed<S : AwsSigner<S, R>, R : Signed<S, R>>(
        open val signer: S,
        open val headers: HeadersBuilder,
        open val params: ParametersBuilder?,
        open val bodyPayload: String,
        open val source: String,
        open val sign: String,
        open val authorization: String,
    ) {

        open fun fill(headers: HeadersBuilder?) {
            if (headers == null) {
                return
            }
            headers.appendAll(this.headers)
            if (authorization.isNotBlank() && headers.authorization().isNullOrBlank()) {
                headers.authorization(authorization)
            }
        }

        open fun fill(urlBuilder: URLBuilder? = null) {
            if (params == null || urlBuilder == null) {
                return
            }
            val up = urlBuilder.parameters
            params?.forEach { k, vs ->
                val uvs = up.getAll(k)

                vs.forEach { v ->
                    if (uvs?.contains(v) != true) {
                        up.append(k, v)
                    }
                }

            }
        }

        open fun fill(headers: HttpHeaders? = null, urlBuilder: URLBuilder? = null) {
            fill(headers)
            fill(urlBuilder)
        }

    }

}
