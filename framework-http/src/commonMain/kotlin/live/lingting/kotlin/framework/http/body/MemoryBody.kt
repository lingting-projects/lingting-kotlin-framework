package live.lingting.kotlin.framework.http.body

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray

/**
 * @author lingting 2026/1/31 17:41
 */
class MemoryBody : Body<ByteArray> {

    val size: Long

    val source: ByteArray

    constructor(bytes: ByteArray) {
        this.size = bytes.size.toLong()
        this.source = bytes
    }

    constructor(string: String) : this(string.encodeToByteArray())

    constructor(path: Path) {
        val metadata = SystemFileSystem.metadataOrNull(path)
        requireNotNull(metadata) { "文件不存在! $path" }
        SystemFileSystem.source(path).use { source ->
            val bytes = source.buffered().readByteArray()
            this.source = bytes
            this.size = bytes.size.toLong()
        }
    }

    override fun length(): Long = size

    override fun source(): ByteArray = source

}
