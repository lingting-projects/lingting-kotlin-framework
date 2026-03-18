package live.lingting.framework.aws.s3.interfaces

import io.ktor.http.HttpMethod
import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.io.files.Path
import live.lingting.framework.async.Async
import live.lingting.framework.async.async
import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.impl.AwsS3PreSignedMultipart
import live.lingting.framework.aws.s3.impl.S3Meta
import live.lingting.framework.aws.s3.multipart.AwsS3MultipartUploadTask
import live.lingting.framework.aws.s3.request.AwsS3ObjectPutRequest
import live.lingting.framework.aws.s3.request.AwsS3PreRequest
import live.lingting.framework.aws.s3.response.AwsS3PreSignedResponse
import live.lingting.framework.data.DataSize
import live.lingting.framework.http.HttpContentTypes
import live.lingting.framework.http.body.Body
import live.lingting.framework.http.body.SourceBody
import live.lingting.framework.http.donwload.HttpMultipartDownload
import live.lingting.framework.io.multipart.MultipartSource
import live.lingting.framework.multipart.Multipart
import live.lingting.framework.multipart.Part
import live.lingting.framework.util.DataSizeUtils.bytes
import live.lingting.framework.util.DurationUtils.days
import kotlin.time.Duration

/**
 * @author lingting 2024-09-19 21:59
 */
interface AwsS3ObjectInterface {

    val defaultExpirePre: Duration
        get() = 1.days

    // region get

    val key: String

    fun publicUrl(): String

    suspend fun head(): S3Meta

    suspend fun download(): Source

    suspend fun download(path: Path): HttpMultipartDownload {
        val meta = head()
        val size = meta.contentLength().bytes
        return download(path, size)
    }

    fun download(path: Path, size: DataSize): HttpMultipartDownload {
        return download(path, AwsUtils.multipart {
            size(size)
        })
    }

    fun download(path: Path, multipart: Multipart): HttpMultipartDownload

    // endregion

    // region put

    suspend fun put(path: Path) = put(path, null as Acl?)

    suspend fun put(path: Path, acl: Acl?) = put(
        SourceBody(
            path
        ), acl, null
    )

    suspend fun put(path: Path, meta: S3Meta?) = put(
        SourceBody(
            path
        ), null, meta
    )

    suspend fun put(body: Body<*>) = put(body, null as Acl?)

    suspend fun put(body: Body<*>, acl: Acl?) = put(body, acl, null)

    suspend fun put(body: Body<*>, meta: S3Meta?) = put(body, null, meta)

    suspend fun put(body: Body<*>, acl: Acl?, meta: S3Meta?) {
        val request = AwsS3ObjectPutRequest(body)
        request.acl = acl
        meta?.run { request.meta.addAll(this) }
        return put(request)
    }

    suspend fun put(request: AwsS3ObjectPutRequest)

    suspend fun delete()

    // endregion

    // region multipart
    suspend fun multipartInit() = multipartInit(null)

    suspend fun multipartInit(meta: S3Meta?): String

    suspend fun multipart(source: MultipartSource) = multipart(
        source,
        async(20)
    )

    suspend fun multipart(source: MultipartSource, async: Async) =
        multipart(source, AwsUtils.MULTIPART_DEFAULT_PART_SIZE, async)

    suspend fun multipart(source: MultipartSource, parSize: DataSize, async: Async): AwsS3MultipartUploadTask {
        return multipart(source, parSize, async, null)
    }

    suspend fun multipart(
        source: MultipartSource,
        parSize: DataSize,
        async: Async,
        acl: Acl?
    ): AwsS3MultipartUploadTask {
        return multipart(source, parSize, async, acl, null)
    }

    suspend fun multipart(
        source: MultipartSource,
        parSize: DataSize,
        async: Async,
        acl: Acl?,
        meta: S3Meta?
    ): AwsS3MultipartUploadTask

    suspend fun multipartUpload(uploadId: String, part: Part, input: RawSource): String

    suspend fun multipartMergeByPart(uploadId: String, map: Map<Part, String>) {
        multipartMergeByPart(uploadId, map, null)
    }

    suspend fun multipartMergeByPart(uploadId: String, map: Map<Part, String>, acl: Acl?) {
        val converted = map.mapKeys { it.key.index }
        multipartMerge(uploadId, converted, acl)
    }

    suspend fun multipartMerge(uploadId: String, map: Map<Long, String>) {
        multipartMerge(uploadId, map, null)
    }

    suspend fun multipartMerge(uploadId: String, map: Map<Long, String>, acl: Acl?)

    suspend fun multipartCancel(uploadId: String)

    // endregion

    // region pre sign

    fun preGet() = preGet(defaultExpirePre)

    fun preGet(expire: Duration): AwsS3PreSignedResponse {
        val r = AwsS3PreRequest(HttpMethod.Get)
        r.expire = expire
        return pre(r)
    }

    fun prePut(): AwsS3PreSignedResponse = prePut(null)

    fun prePut(acl: Acl?): AwsS3PreSignedResponse = prePut(null, null)

    fun prePut(acl: Acl?, meta: S3Meta?): AwsS3PreSignedResponse = prePut(defaultExpirePre, acl, meta)

    fun prePut(expire: Duration): AwsS3PreSignedResponse = prePut(expire, null)

    fun prePut(expire: Duration, acl: Acl?): AwsS3PreSignedResponse = prePut(expire, null, null)

    fun prePut(expire: Duration, acl: Acl?, meta: S3Meta?): AwsS3PreSignedResponse {
        val r = AwsS3PreRequest(HttpMethod.Put)
        r.expire = expire
        r.acl = acl
        if (meta != null) {
            r.meta.addAll(meta)
        }
        r.headers.contentType(HttpContentTypes.STREAM)
        return pre(r)
    }

    suspend fun preMultipart(multipart: Multipart): AwsS3PreSignedMultipart =
        preMultipart(multipart, null)

    suspend fun preMultipart(multipart: Multipart, meta: S3Meta?): AwsS3PreSignedMultipart =
        preMultipart(defaultExpirePre, multipart, meta)

    suspend fun preMultipart(expire: Duration, multipart: Multipart, meta: S3Meta?): AwsS3PreSignedMultipart {
        val uploadId = multipartInit(meta)
        val items = mutableListOf<AwsS3PreSignedMultipart.Item>()
        multipart.parts.forEach { part ->
            val put = preMultipartPut(expire, uploadId, part)
            val item = AwsS3PreSignedMultipart.Item(part, put.url, put.headers)
            items.add(item)
        }
        return AwsS3PreSignedMultipart(
            multipart.size,
            uploadId,
            multipart.partSize,
            items
        )
    }

    fun preMultipartPut(uploadId: String, part: Part): AwsS3PreSignedResponse =
        preMultipartPut(defaultExpirePre, uploadId, part)

    fun preMultipartPut(expire: Duration, uploadId: String, part: Part): AwsS3PreSignedResponse {
        val r = AwsS3PreRequest(HttpMethod.Put)
        r.multipart(uploadId, part)
        return pre(r)
    }

    fun pre(r: AwsS3PreRequest): AwsS3PreSignedResponse

    // endregion

}
