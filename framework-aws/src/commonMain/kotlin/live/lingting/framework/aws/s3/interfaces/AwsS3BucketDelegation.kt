package live.lingting.framework.aws.s3.interfaces

import live.lingting.framework.aws.s3.request.AwsS3ListBucketsRequest
import live.lingting.framework.aws.s3.response.AwsS3ListBucketsResponse

/**
 * @author lingting 2024-09-19 21:55
 */
interface AwsS3BucketDelegation : AwsS3BucketInterface,
    AwsS3Delegation<live.lingting.framework.aws.s3.AwsS3Bucket> {

    override fun use(key: String): AwsS3ObjectInterface {
        return delegation().use(key)
    }

    override suspend fun listBuckets(request: AwsS3ListBucketsRequest): AwsS3ListBucketsResponse {
        return delegation().listBuckets(request)
    }

    override suspend fun multipartList(consumer: ((live.lingting.framework.aws.s3.request.AwsS3SimpleRequest) -> Unit)?): live.lingting.framework.aws.s3.response.AwsS3MultipartListResponse {
        return delegation().multipartList(consumer)
    }

    override suspend fun listObjects(request: live.lingting.framework.aws.s3.request.AwsS3ListObjectsRequest): live.lingting.framework.aws.s3.response.AwsS3ListObjectsResponse {
        return delegation().listObjects(request)
    }
}
