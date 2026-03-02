package live.lingting.kotlin.framework.multipart.task

import kotlinx.io.Sink
import live.lingting.kotlin.framework.async.Async
import live.lingting.kotlin.framework.async.async
import live.lingting.kotlin.framework.io.multipart.FileMultipartSink
import live.lingting.kotlin.framework.io.multipart.FileMultipartSource
import live.lingting.kotlin.framework.io.multipart.MultipartSink
import live.lingting.kotlin.framework.io.multipart.MultipartSource
import live.lingting.kotlin.framework.multipart.Multipart
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2026/2/26 14:50
 */
abstract class SinkMultipartTask : MultipartTask {

    protected val sink: MultipartSink

    @JvmOverloads
    protected constructor(sink: MultipartSink, multipart: Multipart, async: Async = async())
            : super(multipart, async) {
        this.sink = sink
    }

    override suspend fun onPart(task: PartTask) {
        sink.sink(task.part).use {
            onPart(task, it)
        }
    }

    protected abstract suspend fun onPart(task: PartTask, partSink: Sink)

    override suspend fun onMerge() {
        sink.flush()
    }

    fun source(): MultipartSource {
        val status = this.status
        require(status.isCompleted) { "下载任务未完成! status: $status" }
        if (sink is FileMultipartSink) {
            return FileMultipartSource(sink.path)
        }
        return sink.merge()
    }

}
