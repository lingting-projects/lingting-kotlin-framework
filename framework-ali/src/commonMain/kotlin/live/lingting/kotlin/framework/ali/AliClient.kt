package live.lingting.kotlin.framework.ali

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import live.lingting.kotlin.framework.ali.exception.AliException
import live.lingting.kotlin.framework.ali.properties.AliProperties
import live.lingting.kotlin.framework.ali.signer.AliV3Signer
import live.lingting.kotlin.framework.http.api.ApiClient
import live.lingting.kotlin.framework.http.util.HttpHeadersUtils.authorization
import live.lingting.kotlin.framework.http.util.HttpHeadersUtils.to
import live.lingting.kotlin.framework.http.util.HttpUrlUtils.buildPath
import live.lingting.kotlin.framework.http.util.HttpUtils.isOk
import live.lingting.kotlin.framework.http.util.ParametersUtils.to
import live.lingting.kotlin.framework.util.StringUtils

/**
 * @author lingting 2024-09-14 13:49
 */
abstract class AliClient<R : AliRequest> protected constructor(private val properties: AliProperties) :
    ApiClient<R>(properties.host()) {

    protected val ak: String = properties.ak

    protected val sk: String = properties.sk

    protected val token: String? = properties.token

    override fun hostUrlBuilder(): URLBuilder {
        return super.hostUrlBuilder().also {
            it.protocol = if (properties.ssl) URLProtocol.HTTPS else URLProtocol.HTTP
        }
    }

    override suspend fun checkout(r: R, request: HttpRequestBuilder, response: HttpResponse) {
        if (response.isOk) {
            return
        }

        val string = response.bodyAsText()
        val headers = request.headers
        val httpCode = response.status.value
        val msg =
            "[ALI] 请求异常! client: ${this::class.simpleName}; uri: ${request.url.encodedPath}; authorization: ${headers.authorization()}; httpStatus: $httpCode; body: \n${string}"
        log.error { msg }
        throw AliException(msg)
    }

    override suspend fun call(r: R, builder: HttpRequestBuilder): HttpResponse {
        val name = r.name()
        val version = r.version()
        val nonce = r.nonce()

        val headers = builder.headers
        headers["${AliV3Signer.HEADER_PREFIX}-action"] = name
        headers["${AliV3Signer.HEADER_PREFIX}-version"] = version
        headers["${AliV3Signer.HEADER_PREFIX}-signature-nonce"] = nonce

        if (StringUtils.hasText(token)) {
            headers["${AliV3Signer.HEADER_PREFIX}-security-token"] = token
        }

        val signer = AliV3Signer(
            r.method(),
            builder.url.buildPath(),
            headers.to(),
            r.body(),
            builder.url.parameters.to(),
            ak,
            sk
        )

        val signed = signer.signed()
        signed.replace(builder)
        return super.call(r, builder)
    }

}
