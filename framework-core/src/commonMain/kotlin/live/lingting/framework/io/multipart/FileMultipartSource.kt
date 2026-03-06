package live.lingting.framework.io.multipart

import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import live.lingting.framework.util.FileUtils.size

/**
 * @author lingting 2026/2/26 15:25
 */
class FileMultipartSource(val path: Path) : live.lingting.framework.io.multipart.MultipartSource {

    override val size: live.lingting.framework.data.DataSize by lazy { requireNotNull(path.size()) { "文件不存在! $path" } }

    override fun source(): Source {
        val s = SystemFileSystem.source(path)
        return s.buffered()
    }

    override fun source(part: live.lingting.framework.multipart.Part): Source {
        return part.openSource(path)
    }

}
