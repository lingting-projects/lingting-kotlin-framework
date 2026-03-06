package live.lingting.framework.aws.s3.multipart

import kotlinx.io.Source
import live.lingting.framework.async.Async
import live.lingting.framework.async.async
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.AwsS3Object
import live.lingting.framework.io.multipart.MultipartSource
import live.lingting.framework.multipart.Multipart
import live.lingting.framework.multipart.Part
import live.lingting.framework.multipart.task.PartTask
import live.lingting.framework.multipart.task.SourceMultipartTask
import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2024-09-19 20:26
 */
open class AwsS3MultipartUploadTask @JvmOverloads constructor(
    source: MultipartSource,
    multipart: Multipart,
    async: Async = async(10),
    val acl: Acl?,
    protected val s3: AwsS3Object
) : SourceMultipartTask(source, multipart, async) {

    @JvmField
    val uploadId: String = multipart.id

    init {
        retryMaxCount = 3
    }

    override suspend fun onPart(task: PartTask, partSource: Source) {
        val etag = s3.multipartUpload(uploadId, task.part, partSource)
        task.data = etag
    }

    override suspend fun onMerge() {
        val map = mutableMapOf<Part, String>()
        tasks.forEach {
            val etag = it.data
            if (etag is String && etag.isNotBlank()) {
                map[it.part] = etag
            }
        }
        require(map.size == tasks.size) {
            val str = tasks
                .filter {
                    val data = it.data
                    data !is String || data.isBlank()
                }
                .map { it.part.index }
                .joinToString(", ")
            "[S3分片上传] 部分分片上传异常! 未获取到正确的etag值! ${str}"
        }
        s3.multipartMergeByPart(uploadId, map, acl)
    }

    override suspend fun onFailed(t: Throwable?) {
        s3.multipartCancel(uploadId)
    }

}
