package live.lingting.kotlin.framework.aws.s3.interfaces

import live.lingting.kotlin.framework.aws.s3.AwsS3Bucket
import live.lingting.kotlin.framework.aws.s3.request.AwsS3ListObjectsRequest
import live.lingting.kotlin.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.kotlin.framework.aws.s3.response.AwsS3ListObjectsResponse
import live.lingting.kotlin.framework.aws.s3.response.AwsS3MultipartListResponse

/**
 * @author lingting 2024-09-19 21:55
 */
interface AwsS3BucketDelegation : AwsS3BucketInterface, AwsS3Delegation<AwsS3Bucket> {

    override fun use(key: String): AwsS3ObjectInterface {
        return delegation().use(key)
    }

    override suspend fun multipartList(consumer: ((AwsS3SimpleRequest) -> Unit)?): AwsS3MultipartListResponse {
        return delegation().multipartList(consumer)
    }

    override suspend fun listObjects(request: AwsS3ListObjectsRequest): AwsS3ListObjectsResponse {
        return delegation().listObjects(request)
    }
}
