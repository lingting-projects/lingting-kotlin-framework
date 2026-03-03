package live.lingting.kotlin.framework.aws.s3.interfaces

import io.ktor.http.HttpMethod
import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import live.lingting.kotlin.framework.async.Async
import live.lingting.kotlin.framework.async.async
import live.lingting.kotlin.framework.aws.AwsUtils
import live.lingting.kotlin.framework.aws.policy.Acl
import live.lingting.kotlin.framework.aws.s3.AwsS3PreRequest
import live.lingting.kotlin.framework.aws.s3.impl.AwsS3PreSignedMultipart
import live.lingting.kotlin.framework.aws.s3.impl.S3Meta
import live.lingting.kotlin.framework.aws.s3.multipart.AwsS3MultipartUploadTask
import live.lingting.kotlin.framework.aws.s3.request.AwsS3ObjectPutRequest
import live.lingting.kotlin.framework.aws.s3.response.AwsS3PreSignedResponse
import live.lingting.kotlin.framework.data.DataSize
import live.lingting.kotlin.framework.http.body.SourceBody
import live.lingting.kotlin.framework.http.donwload.HttpMultipartDownload
import live.lingting.kotlin.framework.io.multipart.MultipartSource
import live.lingting.kotlin.framework.multipart.Multipart
import live.lingting.kotlin.framework.multipart.Part
import live.lingting.kotlin.framework.util.DataSizeUtils.bytes
import live.lingting.kotlin.framework.util.DurationUtils.days
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

    suspend fun put(path: Path, acl: Acl?) = put(SystemFileSystem.source(path), acl, null)

    suspend fun put(path: Path, meta: S3Meta?) = put(SystemFileSystem.source(path), null, meta)

    suspend fun put(input: RawSource) = put(input, null as Acl?)

    suspend fun put(input: RawSource, acl: Acl?) = put(input, acl, null)

    suspend fun put(input: RawSource, meta: S3Meta?) = put(input, null, meta)

    suspend fun put(input: RawSource, acl: Acl?, meta: S3Meta?) {
        val body = SourceBody(1) { input }
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

    suspend fun multipart(source: MultipartSource) = multipart(source, async(20))

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
        return AwsS3PreSignedMultipart(multipart.size, uploadId, multipart.partSize, items)
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
