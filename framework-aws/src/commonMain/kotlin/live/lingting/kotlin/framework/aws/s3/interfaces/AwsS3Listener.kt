package live.lingting.kotlin.framework.aws.s3.interfaces


import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import live.lingting.kotlin.framework.aws.s3.AwsS3Request
import live.lingting.kotlin.framework.aws.signer.AwsSigner

/**
 * @author lingting 2024/11/5 14:47
 */
interface AwsS3Listener {

    suspend fun onFailed(r: AwsS3Request, request: HttpRequestBuilder, response: HttpResponse)

    fun onSign(r: AwsS3Request, builder: HttpRequestBuilder): AwsSigner<*, *>

}
