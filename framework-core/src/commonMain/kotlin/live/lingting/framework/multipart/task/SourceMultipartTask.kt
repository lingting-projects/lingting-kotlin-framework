package live.lingting.framework.multipart.task

import kotlinx.io.Source
import live.lingting.framework.async.async
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2026/2/26 14:50
 */
abstract class SourceMultipartTask : live.lingting.framework.multipart.task.MultipartTask {

    protected val source: live.lingting.framework.io.multipart.MultipartSource

    @JvmOverloads
    protected constructor(
        source: live.lingting.framework.io.multipart.MultipartSource,
        multipart: live.lingting.framework.multipart.Multipart,
        async: live.lingting.framework.async.Async = _root_ide_package_.live.lingting.framework.async.async()
    )
            : super(multipart, async) {
        this.source = source
    }

    override suspend fun onPart(task: live.lingting.framework.multipart.task.PartTask) {
        source.source(task.part).use {
            onPart(task, it)
        }
    }

    protected abstract suspend fun onPart(task: live.lingting.framework.multipart.task.PartTask, partSource: Source)

}
