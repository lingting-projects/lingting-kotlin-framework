package live.lingting.framework.aws.s3.interfaces

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import live.lingting.framework.aws.s3.request.AwsS3ListBucketsRequest
import live.lingting.framework.aws.s3.request.AwsS3ListObjectsRequest
import live.lingting.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.framework.aws.s3.response.AwsS3ListBucketsResponse
import live.lingting.framework.aws.s3.response.AwsS3ListObjectsResponse
import live.lingting.framework.aws.s3.response.AwsS3MultipartListResponse

/**
 * @author lingting 2024-09-19 21:57
 */
interface AwsS3BucketInterface {

    fun use(key: String): AwsS3ObjectInterface

    suspend fun listBuckets() =
        listBuckets(AwsS3ListBucketsRequest())

    suspend fun listBuckets(prefix: String) = listBuckets(
        AwsS3ListBucketsRequest().also {
            it.prefix = prefix
        })

    suspend fun listBuckets(request: AwsS3ListBucketsRequest): AwsS3ListBucketsResponse

    suspend fun cursorBuckets() = cursorBuckets(AwsS3ListBucketsRequest())

    suspend fun cursorBuckets(prefix: String) = cursorBuckets(
        AwsS3ListBucketsRequest().also {
            it.prefix = prefix
        })

    suspend fun cursorBuckets(request: AwsS3ListBucketsRequest): Flow<AwsS3ListBucketsResponse.Bucket> {
        return flow {
            var currentRequest: AwsS3ListBucketsRequest? = request

            while (currentRequest != null) {
                val response = listBuckets(currentRequest)
                response.buckets.forEach { emit(it) }
                currentRequest = response.nextRequest
            }
        }
    }

    /**
     * 列举所有未完成的分片上传
     * @return k: uploadId, v: k
     */
    suspend fun multipartList() = multipartList(null)

    suspend fun multipartList(consumer: ((AwsS3SimpleRequest) -> Unit)?): AwsS3MultipartListResponse

    suspend fun listObjects() =
        listObjects(AwsS3ListObjectsRequest())

    suspend fun listObjects(prefix: String) = listObjects(
        AwsS3ListObjectsRequest().also {
            it.prefix = prefix
        })

    suspend fun listObjects(request: AwsS3ListObjectsRequest): AwsS3ListObjectsResponse

    suspend fun cursorObjects() = cursorObjects(AwsS3ListObjectsRequest())

    suspend fun cursorObjects(prefix: String) = cursorObjects(
        AwsS3ListObjectsRequest().also {
            it.prefix = prefix
        })

    suspend fun cursorObjects(request: AwsS3ListObjectsRequest): Flow<AwsS3ListObjectsResponse.Content> {
        return flow {
            var currentRequest: AwsS3ListObjectsRequest? = request

            while (currentRequest != null) {
                val response = listObjects(currentRequest)
                response.contents.forEach { emit(it) }
                currentRequest = response.nextRequest
            }
        }
    }

}
