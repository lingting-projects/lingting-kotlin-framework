package live.lingting.kotlin.framework.io.multipart

import kotlinx.io.Sink
import kotlinx.io.files.Path
import live.lingting.kotlin.framework.multipart.Part
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/26 16:08
 */
interface MultipartSink : AutoCloseable {

    companion object {

        @JvmStatic
        fun file(path: Path): FileMultipartSink = FileMultipartSink(path)

        @JvmStatic
        fun memory(): MemoryMultipartSink = MemoryMultipartSink()

    }

    fun sink(part: Part): Sink

    fun write(part: Part, bytes: ByteArray) {
        sink(part).use {
            it.write(bytes)
        }
    }

    /**
     * 合并当前已经写入的所有数据
     */
    fun merge(): MultipartSource

    fun flush()

    fun clear()

}
