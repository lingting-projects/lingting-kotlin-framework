package live.lingting.kotlin.framework.aws.s3.interfaces

import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.io.files.Path
import live.lingting.kotlin.framework.async.Async
import live.lingting.kotlin.framework.aws.policy.Acl
import live.lingting.kotlin.framework.aws.s3.AwsS3Object
import live.lingting.kotlin.framework.aws.s3.AwsS3PreRequest
import live.lingting.kotlin.framework.aws.s3.impl.S3Meta
import live.lingting.kotlin.framework.aws.s3.multipart.AwsS3MultipartUploadTask
import live.lingting.kotlin.framework.aws.s3.request.AwsS3ObjectPutRequest
import live.lingting.kotlin.framework.aws.s3.response.AwsS3PreSignedResponse
import live.lingting.kotlin.framework.data.DataSize
import live.lingting.kotlin.framework.http.donwload.HttpMultipartDownload
import live.lingting.kotlin.framework.io.multipart.MultipartSource
import live.lingting.kotlin.framework.multipart.Multipart
import live.lingting.kotlin.framework.multipart.Part

/**
 * @author lingting 2024-09-19 21:59
 */
interface AwsS3ObjectDelegation : AwsS3ObjectInterface, AwsS3Delegation<AwsS3Object> {

    override val key: String
        get() = delegation().key

    override fun publicUrl(): String {
        return delegation().publicUrl()
    }

    override suspend fun download(): Source {
        return delegation().download()
    }

    override fun download(path: Path, multipart: Multipart): HttpMultipartDownload {
        return delegation().download(path, multipart)
    }

    override suspend fun head(): S3Meta {
        return delegation().head()
    }

    override suspend fun put(request: AwsS3ObjectPutRequest) {
        delegation().put(request)
    }

    override suspend fun delete() {
        delegation().delete()
    }

    override suspend fun multipartInit(meta: S3Meta?): String {
        return delegation().multipartInit(meta)
    }

    override suspend fun multipart(
        source: MultipartSource,
        parSize: DataSize,
        async: Async,
        acl: Acl?,
        meta: S3Meta?
    ): AwsS3MultipartUploadTask {
        return delegation().multipart(source, parSize, async, acl, meta)
    }

    override suspend fun multipartUpload(uploadId: String, part: Part, input: RawSource): String {
        return delegation().multipartUpload(uploadId, part, input)
    }

    override suspend fun multipartMerge(uploadId: String, map: Map<Long, String>, acl: Acl?) {
        delegation().multipartMerge(uploadId, map, acl)
    }

    override suspend fun multipartCancel(uploadId: String) {
        delegation().multipartCancel(uploadId)
    }

    override fun pre(r: AwsS3PreRequest): AwsS3PreSignedResponse {
        return delegation().pre(r)
    }

}
