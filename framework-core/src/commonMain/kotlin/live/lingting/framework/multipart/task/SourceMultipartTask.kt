package live.lingting.framework.multipart.task

import kotlinx.io.Source
import live.lingting.framework.async.Async
import live.lingting.framework.async.async
import live.lingting.framework.io.multipart.MultipartSource
import live.lingting.framework.multipart.Multipart
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2026/2/26 14:50
 */
abstract class SourceMultipartTask : MultipartTask {

    protected val source: MultipartSource

    @JvmOverloads
    protected constructor(
        source: MultipartSource,
        multipart: Multipart,
        async: Async = async()
    )
            : super(multipart, async) {
        this.source = source
    }

    override suspend fun onPart(task: PartTask) {
        source.source(task.part).use {
            onPart(task, it)
        }
    }

    protected abstract suspend fun onPart(task: PartTask, partSource: Source)

}
