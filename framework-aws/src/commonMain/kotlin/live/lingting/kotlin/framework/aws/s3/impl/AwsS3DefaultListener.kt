package live.lingting.kotlin.framework.aws.s3.impl


import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodedPath
import live.lingting.kotlin.framework.aws.AwsS3Client
import live.lingting.kotlin.framework.aws.exception.AwsS3Exception
import live.lingting.kotlin.framework.aws.s3.AwsS3Request
import live.lingting.kotlin.framework.aws.s3.interfaces.AwsS3Listener
import live.lingting.kotlin.framework.aws.signer.AwsSigner
import live.lingting.kotlin.framework.aws.signer.AwsV4Signer
import live.lingting.kotlin.framework.http.util.HttpHeadersUtils.to
import live.lingting.kotlin.framework.http.util.ParametersUtils.to
import live.lingting.kotlin.framework.util.LoggerUtils.logger
import kotlin.jvm.JvmField

/**
 * @author lingting 2024/11/5 14:48
 */
open class AwsS3DefaultListener(@JvmField protected val client: AwsS3Client) : AwsS3Listener {

    @JvmField
    protected val log = client.logger()

    override suspend fun onFailed(r: AwsS3Request, request: HttpRequestBuilder, response: HttpResponse) {
        val httpStatus = response.status.value
        val string = response.bodyAsText()
        val msg = "[S3] S3请求异常! uri: ${request.url.encodedPath}; httpStatus: $httpStatus;"
        log.error {
            "$msg body: \n$string"
        }
        throw AwsS3Exception(msg)
    }


    override fun onSign(r: AwsS3Request, builder: HttpRequestBuilder): AwsSigner<*, *> {
        val properties = client.properties

        return AwsV4Signer(
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
