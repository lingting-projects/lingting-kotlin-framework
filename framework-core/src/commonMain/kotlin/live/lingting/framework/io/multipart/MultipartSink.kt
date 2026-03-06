package live.lingting.framework.io.multipart

import kotlinx.io.Sink
import kotlinx.io.files.Path
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/26 16:08
 */
interface MultipartSink : AutoCloseable {

    companion object {

        @JvmStatic
        fun file(path: Path): live.lingting.framework.io.multipart.FileMultipartSink =
            _root_ide_package_.live.lingting.framework.io.multipart.FileMultipartSink(path)

        @JvmStatic
        fun memory(): live.lingting.framework.io.multipart.MemoryMultipartSink =
            _root_ide_package_.live.lingting.framework.io.multipart.MemoryMultipartSink()

    }

    fun sink(part: live.lingting.framework.multipart.Part): Sink

    fun write(part: live.lingting.framework.multipart.Part, bytes: ByteArray) {
        sink(part).use {
            it.write(bytes)
        }
    }

    /**
     * 合并当前已经写入的所有数据
     */
    fun merge(): live.lingting.framework.io.multipart.MultipartSource

    fun flush()

    fun clear()

}
