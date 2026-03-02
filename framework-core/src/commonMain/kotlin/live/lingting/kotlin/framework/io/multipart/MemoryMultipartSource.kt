package live.lingting.kotlin.framework.io.multipart

import kotlinx.io.Buffer
import kotlinx.io.Source
import live.lingting.kotlin.framework.multipart.Part

/**
 * @author lingting 2026/2/26 15:30
 */
class MemoryMultipartSource(val source: Source) : MultipartSource {

    constructor(bytes: ByteArray) : this(Buffer().apply { write(bytes) })

    constructor(content: String) : this(content.encodeToByteArray())

    override fun source(): Source {
        return source.peek()
    }

    override fun source(part: Part): Source {
        return part.pickSource(source)
    }

}
