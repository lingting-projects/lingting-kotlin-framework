package live.lingting.framework.huawei.obs


import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodedPath
import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.aws.s3.enums.HostStyle
import live.lingting.framework.aws.s3.impl.AwsS3DefaultListener
import live.lingting.framework.aws.signer.AwsSigner
import live.lingting.framework.http.util.HttpHeadersUtils.to
import live.lingting.framework.http.util.HttpUrlUtils.buildPath
import live.lingting.framework.http.util.ParametersUtils.to
import live.lingting.framework.huawei.exception.HuaweiObsException

/**
 * @author lingting 2024/11/5 14:54
 */
class HuaweiObsListener(val obs: HuaweiObs<*>) : AwsS3DefaultListener(obs.delegation()) {

    override suspend fun onFailed(r: AwsS3Request, request: HttpRequestBuilder, response: HttpResponse) {
        val httpStatus = response.status.value
        val string = response.bodyAsText()
        val msg = "[OBS] HUAWEI 请求异常!  uri: ${request.url.encodedPath}; httpStatus: $httpStatus;"
        log.error {
            "$msg body: \n$string"
        }
        throw HuaweiObsException(msg)
    }

    override fun onSign(r: AwsS3Request, builder: HttpRequestBuilder): AwsSigner<*, *> {
        val properties = obs.properties

        val headers = builder.headers.to()
        headers.keys().forEach { name ->
            if (name.startsWith(AwsUtils.HEADER_PREFIX)) {
                val newName = name.replace(AwsUtils.HEADER_PREFIX, HuaweiObs.HEADER_PREFIX)
                headers.replace(name, newName)
            }
        }

        val url = builder.url
        val path = if (properties.hostStyle != HostStyle.VIRTUAL) url.buildPath()
        else "/${properties.bucket}${url.buildPath()}"

        return HuaweiObsSigner(
            builder.method,
            path,
            headers,
            null,
            url.parameters.to(),
            properties.ak,
            properties.sk
        )
    }

}
