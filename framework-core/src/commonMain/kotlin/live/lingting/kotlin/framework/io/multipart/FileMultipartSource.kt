package live.lingting.kotlin.framework.io.multipart

import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import live.lingting.kotlin.framework.data.DataSize
import live.lingting.kotlin.framework.multipart.Part
import live.lingting.kotlin.framework.util.FileUtils.size

/**
 * @author lingting 2026/2/26 15:25
 */
class FileMultipartSource(val path: Path) : MultipartSource {

    override val size: DataSize by lazy { requireNotNull(path.size()) { "文件不存在! $path" } }

    override fun source(): Source {
        val s = SystemFileSystem.source(path)
        return s.buffered()
    }

    override fun source(part: Part): Source {
        return part.openSource(path)
    }

}
