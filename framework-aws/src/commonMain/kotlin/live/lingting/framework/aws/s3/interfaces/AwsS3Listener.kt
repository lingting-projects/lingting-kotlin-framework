package live.lingting.framework.aws.s3.interfaces


import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse

/**
 * @author lingting 2024/11/5 14:47
 */
interface AwsS3Listener {

    suspend fun onFailed(
        r: live.lingting.framework.aws.s3.AwsS3Request,
        request: HttpRequestBuilder,
        response: HttpResponse
    )

    fun onSign(
        r: live.lingting.framework.aws.s3.AwsS3Request,
        builder: HttpRequestBuilder
    ): live.lingting.framework.aws.signer.AwsSigner<*, *>

}
