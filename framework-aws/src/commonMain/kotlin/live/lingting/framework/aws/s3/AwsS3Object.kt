package live.lingting.framework.aws.s3

import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.supportsRequestBody
import io.ktor.utils.io.readBuffer
import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.io.files.Path
import live.lingting.framework.http.util.HttpExtraUtils.convert
import live.lingting.framework.http.util.HttpHeadersUtils.etag
import live.lingting.framework.http.util.HttpHeadersUtils.to
import live.lingting.framework.xml.XmlExtraUtils.xmlToObj

/**
 * @author lingting 2024-09-19 15:09
 */
class AwsS3Object(properties: live.lingting.framework.aws.properties.S3Properties, override val key: String) :
    AwsS3(properties),
    live.lingting.framework.aws.s3.interfaces.AwsS3ObjectInterface {

    val publicUrl: String = properties.urlBuilder().appendPathSegments(key).build().toString()

    override fun onCall(r: AwsS3Request) {
        r.key = key
        if (acl != null && r.acl == null) {
            val method = r.method()
            if (method.supportsRequestBody) {
                r.acl = acl
            }
        }
    }

    override fun publicUrl(): String {
        return publicUrl
    }

    override suspend fun head(): AwsS3Meta {
        val request = _root_ide_package_.live.lingting.framework.aws.s3.request.AwsS3SimpleRequest(HttpMethod.Get)
        request.headers.range(0, 0)
        val response = call(request)
        val headers = response.headers.to()
        // 当前实现 content-length 返回有问题, ktor cio 引擎的原因, 先手动适配
        val size = headers.contentSize()
        headers.contentLength(size.bytes)
        return AwsS3Meta(headers)
    }

    override suspend fun download(): Source {
        val request = _root_ide_package_.live.lingting.framework.aws.s3.request.AwsS3SimpleRequest(HttpMethod.Get)
        val response = call(request)
        return response.bodyAsChannel().readBuffer()
    }

    override fun download(
        path: Path,
        multipart: live.lingting.framework.multipart.Multipart
    ): live.lingting.framework.http.donwload.HttpMultipartDownload {
        val url = preGet().url
        return live.lingting.framework.http.donwload.HttpMultipartDownload(Url(url), path, multipart)
    }

    override suspend fun put(request: live.lingting.framework.aws.s3.request.AwsS3ObjectPutRequest) {
        call(request)
    }

    override suspend fun delete() {
        val request = _root_ide_package_.live.lingting.framework.aws.s3.request.AwsS3SimpleRequest(HttpMethod.Delete)
        call(request)
    }

    override suspend fun multipartInit(meta: live.lingting.framework.aws.s3.impl.S3Meta?): String {
        val request = _root_ide_package_.live.lingting.framework.aws.s3.request.AwsS3SimpleRequest(HttpMethod.Post)
        request.params.add("uploads")
        meta?.run { request.meta.addAll(this) }
        return call(request).convert {
            it.xmlToObj<live.lingting.framework.aws.s3.response.AwsS3MultipartInitResponse>()
        }.uploadId
    }

    override suspend fun multipart(
        source: live.lingting.framework.io.multipart.MultipartSource,
        parSize: live.lingting.framework.data.DataSize,
        async: live.lingting.framework.async.Async,
        acl: live.lingting.framework.aws.policy.Acl?,
        meta: live.lingting.framework.aws.s3.impl.S3Meta?
    ): live.lingting.framework.aws.s3.multipart.AwsS3MultipartUploadTask {
        val uploadId = multipartInit(meta)

        val multipart = live.lingting.framework.aws.AwsUtils.multipart {
            id(uploadId)
            size(source.size)
            partSize(parSize)
        }

        val task = _root_ide_package_.live.lingting.framework.aws.s3.multipart.AwsS3MultipartUploadTask(
            source,
            multipart,
            async,
            acl,
            this
        )
        task.start()
        return task
    }

    /**
     * 上传分片
     * @return 合并用的 etag
     */
    override suspend fun multipartUpload(
        uploadId: String,
        part: live.lingting.framework.multipart.Part,
        input: RawSource
    ): String {
        val body = live.lingting.framework.http.body.SourceBody(part.size.bytes) { input }
        val request = _root_ide_package_.live.lingting.framework.aws.s3.request.AwsS3ObjectPutRequest(body)
        request.multipart(uploadId, part)
        val response = call(request)
        val headers = response.headers
        return headers.etag()!!
    }

    /**
     * 合并分片
     * @param map key: part. value: etag
     */
    override suspend fun multipartMerge(
        uploadId: String,
        map: Map<Long, String>,
        acl: live.lingting.framework.aws.policy.Acl?
    ) {
        val request = _root_ide_package_.live.lingting.framework.aws.s3.request.AwsS3MultipartMergeRequest()
        request.uploadId = uploadId
        request.eTagMap = map
        request.acl = acl
        call(request)
    }

    override suspend fun multipartCancel(uploadId: String) {
        val request = _root_ide_package_.live.lingting.framework.aws.s3.request.AwsS3SimpleRequest(HttpMethod.Delete)
        request.params.add("uploadId", uploadId)
        call(request)
    }

}
