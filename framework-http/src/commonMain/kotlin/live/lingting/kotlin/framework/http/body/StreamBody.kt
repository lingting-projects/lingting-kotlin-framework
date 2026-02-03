package live.lingting.kotlin.framework.http.body

import kotlinx.io.RawSource
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

/**
 * @author lingting 2026/1/31 17:41
 */
class StreamBody : Body<RawSource> {

    val size: Long

    val supplier: () -> RawSource

    constructor(size: Long, supplier: () -> RawSource) {
        this.size = size
        this.supplier = supplier
    }

    constructor(path: String) : this(Path(path))

    constructor(size: Long, path: String) : this(size, Path(path))

    constructor(path: Path) {
        val metadata = SystemFileSystem.metadataOrNull(path)
        requireNotNull(metadata) { "文件不存在! $path" }
        this.size = metadata.size
        this.supplier = { SystemFileSystem.source(path) }
    }

    constructor(size: Long, path: Path) {
        this.size = size
        this.supplier = { SystemFileSystem.source(path) }
    }

    override fun length(): Long = size

    override fun source(): RawSource = supplier()

}
