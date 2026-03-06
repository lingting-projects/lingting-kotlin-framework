package live.lingting.framework.aws.s3.impl


import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodedPath
import live.lingting.framework.http.util.HttpHeadersUtils.to
import live.lingting.framework.http.util.ParametersUtils.to
import live.lingting.framework.util.LoggerUtils.logger
import kotlin.jvm.JvmField

/**
 * @author lingting 2024/11/5 14:48
 */
open class AwsS3DefaultListener(@JvmField protected val client: live.lingting.framework.aws.s3.AwsS3) :
    live.lingting.framework.aws.s3.interfaces.AwsS3Listener {

    @JvmField
    protected val log = client.logger()

    override suspend fun onFailed(
        r: live.lingting.framework.aws.s3.AwsS3Request,
        request: HttpRequestBuilder,
        response: HttpResponse
    ) {
        val httpStatus = response.status.value
        val string = response.bodyAsText()
        val msg = "[S3] AWS 请求异常! uri: ${request.url.encodedPath}; httpStatus: $httpStatus;"
        log.error {
            "$msg body: \n$string"
        }
        throw _root_ide_package_.live.lingting.framework.aws.exception.AwsS3Exception(msg)
    }


    override fun onSign(
        r: live.lingting.framework.aws.s3.AwsS3Request,
        builder: HttpRequestBuilder
    ): live.lingting.framework.aws.signer.AwsSigner<*, *> {
        val properties = client.properties

        return _root_ide_package_.live.lingting.framework.aws.signer.AwsV4Signer(
            builder.method,
            builder.url.encodedPath,
            builder.headers.to(),
            null,
            builder.url.parameters.to(),
            properties.region,
            properties.ak,
            properties.sk,
            "s3"
        )
    }

}
