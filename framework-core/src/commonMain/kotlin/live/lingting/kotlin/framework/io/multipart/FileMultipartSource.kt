package live.lingting.kotlin.framework.io.multipart

import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import live.lingting.kotlin.framework.multipart.Part

/**
 * @author lingting 2026/2/26 15:25
 */
class FileMultipartSource(val path: Path) : MultipartSource {

    override fun source(): Source {
        val s = SystemFileSystem.source(path)
        return s.buffered()
    }

    override fun source(part: Part): Source {
        return part.openSource(path)
    }

}
