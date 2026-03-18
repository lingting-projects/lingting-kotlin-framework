package live.lingting.framework.aws

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ParametersBuilder
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import live.lingting.framework.aws.exception.AwsException
import live.lingting.framework.aws.properties.AwsProperties
import live.lingting.framework.aws.signer.AwsV4Signer
import live.lingting.framework.http.QueryBuilder
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.http.util.HttpHeadersUtils.authorization
import live.lingting.framework.http.util.HttpHeadersUtils.to
import live.lingting.framework.http.util.HttpUrlUtils.buildPath
import live.lingting.framework.http.util.HttpUtils.isOk
import live.lingting.framework.http.util.ParametersUtils.copy
import live.lingting.framework.value.multi.StringMultiValue

/**
 * @author lingting 2025/6/3 15:41
 */
abstract class AwsClient<R : AwsRequest>(private val properties: AwsProperties) : ApiClient<R>(properties.host()) {

    protected val ak: String = properties.ak

    protected val sk: String = properties.sk

    protected val token: String? = properties.token

    protected val region = properties.region.let { it.ifBlank { AwsProperties.REGION } }

    override fun hostUrlBuilder(host: String): URLBuilder {
        return super.hostUrlBuilder(host).also {
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
            "[AWS] 请求异常! client: ${this::class.simpleName}; uri: ${request.url.encodedPath}; authorization: ${headers.authorization()}; httpStatus: $httpCode; body: \n${string}"
        log.error { msg }
        throw AwsException(msg)
    }

    fun body(value: ParametersBuilder): MemoryBody {
        val query = QueryBuilder(value).also {
            it.sort = true
            it.indexSuffix = true
            it.indexSuffixAll = false
            it.indexMatchNames =
                setOf("PolicyArns.member", "ProvidedContexts.member", "Tags.member", "TransitiveTagKeys.member")
        }.build()
        return MemoryBody(query)
    }

    override suspend fun call(r: R, builder: HttpRequestBuilder): HttpResponse {
        val urlBuilder = builder.url
        val parameters = urlBuilder.parameters.copy()
        urlBuilder.parameters.clear()
        parameters["Action"] = r.action()
        parameters["Version"] = r.version()
        val body = body(parameters)

        val signer = AwsV4Signer(
            r.method(),
            urlBuilder.buildPath(),
            builder.headers.to(),
            body,
            StringMultiValue(),
            region,
            ak,
            sk,
            service()
        )

        val signed = signer.signed()
        signed.replace(builder)
        return super.call(r, builder)
    }

    abstract fun service(): String

}
