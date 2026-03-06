package live.lingting.framework.io.multipart

import kotlinx.io.Source
import kotlinx.io.files.Path
import kotlinx.io.readByteArray
import live.lingting.framework.data.DataSize
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/26 15:24
 */
interface MultipartSource {

    companion object {

        @JvmStatic
        fun file(path: Path): FileMultipartSource = FileMultipartSource(path)

        @JvmStatic
        fun memory(source: Source, size: DataSize): MemoryMultipartSource =
            MemoryMultipartSource(source, size)

        @JvmStatic
        fun memory(bytes: ByteArray): MemoryMultipartSource = MemoryMultipartSource(bytes)

        @JvmStatic
        fun memory(content: String): MemoryMultipartSource = MemoryMultipartSource(content)

    }

    val size: DataSize

    /**
     * 获取完整源
     */
    fun source(): Source

    /**
     * 读取源所有数据
     */
    fun bytes(): ByteArray {
        return source().use {
            it.readByteArray()
        }
    }

    /**
     * 获取分片源
     */
    fun source(part: live.lingting.framework.multipart.Part): Source

    /**
     * 读取分片数据
     */
    fun bytes(part: live.lingting.framework.multipart.Part): ByteArray {
        return source(part).use { it.readByteArray() }
    }

}
