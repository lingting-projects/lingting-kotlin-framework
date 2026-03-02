package live.lingting.kotlin.framework.io.multipart

import kotlinx.io.Source
import kotlinx.io.files.Path
import kotlinx.io.readByteArray
import live.lingting.kotlin.framework.multipart.Part
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/26 15:24
 */
interface MultipartSource {

    companion object {

        @JvmStatic
        fun file(path: Path): FileMultipartSource = FileMultipartSource(path)

        @JvmStatic
        fun memory(source: Source): MemoryMultipartSource = MemoryMultipartSource(source)

        @JvmStatic
        fun memory(bytes: ByteArray): MemoryMultipartSource = MemoryMultipartSource(bytes)

        @JvmStatic
        fun memory(content: String): MemoryMultipartSource = MemoryMultipartSource(content)

    }

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
    fun source(part: Part): Source

    /**
     * 读取分片数据
     */
    fun bytes(part: Part): ByteArray {
        return source(part).use { it.readByteArray() }
    }

}
