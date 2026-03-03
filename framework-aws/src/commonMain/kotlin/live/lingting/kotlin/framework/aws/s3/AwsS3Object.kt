package live.lingting.kotlin.framework.aws.s3

import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.supportsRequestBody
import io.ktor.utils.io.readBuffer
import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.io.files.Path
import live.lingting.kotlin.framework.async.Async
import live.lingting.kotlin.framework.aws.AwsS3Client
import live.lingting.kotlin.framework.aws.AwsUtils
import live.lingting.kotlin.framework.aws.policy.Acl
import live.lingting.kotlin.framework.aws.properties.S3Properties
import live.lingting.kotlin.framework.aws.s3.impl.S3Meta
import live.lingting.kotlin.framework.aws.s3.interfaces.AwsS3ObjectInterface
import live.lingting.kotlin.framework.aws.s3.multipart.AwsS3MultipartUploadTask
import live.lingting.kotlin.framework.aws.s3.request.AwsS3MultipartMergeRequest
import live.lingting.kotlin.framework.aws.s3.request.AwsS3ObjectPutRequest
import live.lingting.kotlin.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.kotlin.framework.aws.s3.response.AwsS3MultipartInitResponse
import live.lingting.kotlin.framework.data.DataSize
import live.lingting.kotlin.framework.http.body.SourceBody
import live.lingting.kotlin.framework.http.donwload.HttpMultipartDownload
import live.lingting.kotlin.framework.http.util.HttpExtraUtils.convert
import live.lingting.kotlin.framework.http.util.HttpHeadersUtils.etag
import live.lingting.kotlin.framework.io.multipart.MultipartSource
import live.lingting.kotlin.framework.multipart.Multipart
import live.lingting.kotlin.framework.multipart.Part
import live.lingting.kotlin.framework.xml.XmlExtraUtils.xmlToObj

/**
 * @author lingting 2024-09-19 15:09
 */
class AwsS3Object(properties: S3Properties, override val key: String) : AwsS3Client(properties), AwsS3ObjectInterface {

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
        val request = AwsS3SimpleRequest(HttpMethod.Head)
        val response = call(request)
        val headers = response.headers
        return AwsS3Meta(headers)
    }

    override suspend fun download(): Source {
        val request = AwsS3SimpleRequest(HttpMethod.Get)
        val response = call(request)
        return response.bodyAsChannel().readBuffer()
    }

    override fun download(path: Path, multipart: Multipart): HttpMultipartDownload {
        val url = preGet().url
        return HttpMultipartDownload(Url(url), path, multipart)
    }

    override suspend fun put(request: AwsS3ObjectPutRequest) {
        call(request)
    }

    override suspend fun delete() {
        val request = AwsS3SimpleRequest(HttpMethod.Delete)
        call(request)
    }

    override suspend fun multipartInit(meta: S3Meta?): String {
        val request = AwsS3SimpleRequest(HttpMethod.Post)
        request.params.add("uploads")
        meta?.run { request.meta.addAll(this) }
        return call(request).convert {
            it.xmlToObj<AwsS3MultipartInitResponse>()
        }.uploadId
    }

    override suspend fun multipart(
        source: MultipartSource,
        parSize: DataSize,
        async: Async,
        acl: Acl?,
        meta: S3Meta?
    ): AwsS3MultipartUploadTask {
        val uploadId = multipartInit(meta)

        val multipart = AwsUtils.multipart {
            id(uploadId)
            partSize(parSize)
        }

        val task = AwsS3MultipartUploadTask(source, multipart, async, acl, this)
        task.start()
        return task
    }

    /**
     * 上传分片
     * @return 合并用的 etag
     */
    override suspend fun multipartUpload(uploadId: String, part: Part, input: RawSource): String {
        val body = SourceBody(1) { input }
        val request = AwsS3ObjectPutRequest(body)
        request.multipart(uploadId, part)
        val response = call(request)
        val headers = response.headers
        return headers.etag()!!
    }

    /**
     * 合并分片
     * @param map key: part. value: etag
     */
    override suspend fun multipartMerge(uploadId: String, map: Map<Long, String>, acl: Acl?) {
        val request = AwsS3MultipartMergeRequest()
        request.uploadId = uploadId
        request.eTagMap = map
        request.acl = acl
        call(request)
    }

    override suspend fun multipartCancel(uploadId: String) {
        val request = AwsS3SimpleRequest(HttpMethod.Delete)
        request.params.add("uploadId", uploadId)
        call(request)
    }

}
