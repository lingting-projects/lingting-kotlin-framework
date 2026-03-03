package live.lingting.kotlin.framework.aws

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ParametersBuilder
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.encodedPath
import live.lingting.framework.aws.exception.AwsException
import live.lingting.kotlin.framework.aws.properties.AwsProperties
import live.lingting.kotlin.framework.aws.signer.AwsV4Signer
import live.lingting.kotlin.framework.http.QueryBuilder
import live.lingting.kotlin.framework.http.api.ApiClient
import live.lingting.kotlin.framework.http.body.MemoryBody
import live.lingting.kotlin.framework.http.util.HttpHeadersUtils.authorization
import live.lingting.kotlin.framework.http.util.HttpHeadersUtils.to
import live.lingting.kotlin.framework.http.util.HttpUrlUtils.buildPath
import live.lingting.kotlin.framework.http.util.HttpUtils.isOk
import live.lingting.kotlin.framework.http.util.ParametersUtils.copy

/**
 * @author lingting 2025/6/3 15:41
 */
abstract class AwsClient<R : AwsRequest>(properties: AwsProperties) : ApiClient<R>(properties.host()) {

    protected val ak: String = properties.ak

    protected val sk: String = properties.sk

    protected val token: String? = properties.token

    protected val region = properties.region.let { it.ifBlank { AwsProperties.REGION } }

    override val hostUrl: Url = URLBuilder(host).also {
        it.protocol = if (properties.ssl) URLProtocol.HTTPS else URLProtocol.HTTP
    }.build()

    override suspend fun checkout(r: R, request: HttpRequestBuilder, response: HttpResponse) {
        if (response.isOk) {
            return
        }

        val string = response.bodyAsText()
        val headers = request.headers
        val httpCode = response.status.value
        log.error {
            "aws 请求异常! client: ${this::class::simpleName}; uri: ${request.url.encodedPath}; authorization: ${headers.authorization()}; httpStatus: $httpCode; body: \n${string}"
        }
        throw AwsException("request error! action: ${r.action()}; code: $httpCode")
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
            QueryBuilder(),
            region,
            ak,
            sk,
            service()
        )

        val signed = signer.signed()
        signed.fill(builder)
        return super.call(r, builder)
    }

    abstract fun service(): String

}
