package live.lingting.framework.aws.s3.interfaces

import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.io.files.Path
import live.lingting.framework.aws.s3.request.AwsS3PreRequest

/**
 * @author lingting 2024-09-19 21:59
 */
interface AwsS3ObjectDelegation : AwsS3ObjectInterface,
    AwsS3Delegation<live.lingting.framework.aws.s3.AwsS3Object> {

    override val key: String
        get() = delegation().key

    override fun publicUrl(): String {
        return delegation().publicUrl()
    }

    override suspend fun download(): Source {
        return delegation().download()
    }

    override fun download(
        path: Path,
        multipart: live.lingting.framework.multipart.Multipart
    ): live.lingting.framework.http.donwload.HttpMultipartDownload {
        return delegation()
            .download(path, multipart)
    }

    override suspend fun head(): live.lingting.framework.aws.s3.impl.S3Meta {
        return delegation().head()
    }

    override suspend fun put(request: live.lingting.framework.aws.s3.request.AwsS3ObjectPutRequest) {
        delegation().put(request)
    }

    override suspend fun delete() {
        delegation().delete()
    }

    override suspend fun multipartInit(meta: live.lingting.framework.aws.s3.impl.S3Meta?): String {
        return delegation().multipartInit(meta)
    }

    override suspend fun multipart(
        source: live.lingting.framework.io.multipart.MultipartSource,
        parSize: live.lingting.framework.data.DataSize,
        async: live.lingting.framework.async.Async,
        acl: live.lingting.framework.aws.policy.Acl?,
        meta: live.lingting.framework.aws.s3.impl.S3Meta?
    ): live.lingting.framework.aws.s3.multipart.AwsS3MultipartUploadTask {
        return delegation()
            .multipart(source, parSize, async, acl, meta)
    }

    override suspend fun multipartUpload(
        uploadId: String,
        part: live.lingting.framework.multipart.Part,
        input: RawSource
    ): String {
        return delegation()
            .multipartUpload(uploadId, part, input)
    }

    override suspend fun multipartMerge(
        uploadId: String,
        map: Map<Long, String>,
        acl: live.lingting.framework.aws.policy.Acl?
    ) {
        delegation()
            .multipartMerge(uploadId, map, acl)
    }

    override suspend fun multipartCancel(uploadId: String) {
        delegation().multipartCancel(uploadId)
    }

    override fun pre(r: AwsS3PreRequest): live.lingting.framework.aws.s3.response.AwsS3PreSignedResponse {
        return delegation().pre(r)
    }

}
