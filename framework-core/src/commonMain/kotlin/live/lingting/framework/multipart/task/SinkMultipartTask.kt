package live.lingting.framework.multipart.task

import kotlinx.io.Sink
import live.lingting.framework.async.async
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2026/2/26 14:50
 */
abstract class SinkMultipartTask : live.lingting.framework.multipart.task.MultipartTask {

    protected val sink: live.lingting.framework.io.multipart.MultipartSink

    @JvmOverloads
    protected constructor(
        sink: live.lingting.framework.io.multipart.MultipartSink,
        multipart: live.lingting.framework.multipart.Multipart,
        async: live.lingting.framework.async.Async = _root_ide_package_.live.lingting.framework.async.async()
    )
            : super(multipart, async) {
        this.sink = sink
    }

    override suspend fun onPart(task: live.lingting.framework.multipart.task.PartTask) {
        sink.sink(task.part).use {
            onPart(task, it)
        }
    }

    protected abstract suspend fun onPart(task: live.lingting.framework.multipart.task.PartTask, partSink: Sink)

    override suspend fun onMerge() {
        sink.flush()
    }

    fun source(): live.lingting.framework.io.multipart.MultipartSource {
        val status = this.status
        require(status.isCompleted) { "下载任务未完成! status: $status" }
        if (sink is live.lingting.framework.io.multipart.FileMultipartSink) {
            return _root_ide_package_.live.lingting.framework.io.multipart.FileMultipartSource(sink.path)
        }
        return sink.merge()
    }

}
