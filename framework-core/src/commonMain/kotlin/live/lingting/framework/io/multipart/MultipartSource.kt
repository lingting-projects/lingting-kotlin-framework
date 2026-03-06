package live.lingting.framework.io.multipart

import kotlinx.io.Source
import kotlinx.io.files.Path
import kotlinx.io.readByteArray
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/26 15:24
 */
interface MultipartSource {

    companion object {

        @JvmStatic
        fun file(path: Path): live.lingting.framework.io.multipart.FileMultipartSource =
            _root_ide_package_.live.lingting.framework.io.multipart.FileMultipartSource(path)

        @JvmStatic
        fun memory(
            source: Source,
            size: live.lingting.framework.data.DataSize
        ): live.lingting.framework.io.multipart.MemoryMultipartSource =
            _root_ide_package_.live.lingting.framework.io.multipart.MemoryMultipartSource(source, size)

        @JvmStatic
        fun memory(bytes: ByteArray): live.lingting.framework.io.multipart.MemoryMultipartSource =
            _root_ide_package_.live.lingting.framework.io.multipart.MemoryMultipartSource(bytes)

        @JvmStatic
        fun memory(content: String): live.lingting.framework.io.multipart.MemoryMultipartSource =
            _root_ide_package_.live.lingting.framework.io.multipart.MemoryMultipartSource(content)

    }

    val size: live.lingting.framework.data.DataSize

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
