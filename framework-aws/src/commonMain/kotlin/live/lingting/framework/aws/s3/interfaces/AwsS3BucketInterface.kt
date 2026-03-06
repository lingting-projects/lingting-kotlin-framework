package live.lingting.framework.aws.s3.interfaces

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * @author lingting 2024-09-19 21:57
 */
interface AwsS3BucketInterface {

    fun use(key: String): live.lingting.framework.aws.s3.interfaces.AwsS3ObjectInterface

    /**
     * 列举所有未完成的分片上传
     * @return k: uploadId, v: k
     */
    suspend fun multipartList() = multipartList(null)

    suspend fun multipartList(consumer: ((live.lingting.framework.aws.s3.request.AwsS3SimpleRequest) -> Unit)?): live.lingting.framework.aws.s3.response.AwsS3MultipartListResponse

    suspend fun listObjects() =
        listObjects(_root_ide_package_.live.lingting.framework.aws.s3.request.AwsS3ListObjectsRequest())

    suspend fun listObjects(prefix: String) = listObjects(
        _root_ide_package_.live.lingting.framework.aws.s3.request.AwsS3ListObjectsRequest().also {
            it.prefix = prefix
        })

    suspend fun listObjects(request: live.lingting.framework.aws.s3.request.AwsS3ListObjectsRequest): live.lingting.framework.aws.s3.response.AwsS3ListObjectsResponse

    suspend fun cursorObjects(request: live.lingting.framework.aws.s3.request.AwsS3ListObjectsRequest): Flow<live.lingting.framework.aws.s3.response.AwsS3ListObjectsResponse.Content> {
        return flow {
            var currentRequest: live.lingting.framework.aws.s3.request.AwsS3ListObjectsRequest? = request

            while (currentRequest != null) {
                val response = listObjects(currentRequest)
                response.contents.forEach { emit(it) }
                currentRequest = response.nextRequest
            }
        }
    }

}
