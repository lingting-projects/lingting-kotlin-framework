package live.lingting.kotlin.framework.io.multipart

import kotlinx.io.Buffer
import kotlinx.io.Source
import live.lingting.kotlin.framework.data.DataSize
import live.lingting.kotlin.framework.multipart.Part
import live.lingting.kotlin.framework.util.DataSizeUtils.bytes

/**
 * @author lingting 2026/2/26 15:30
 */
class MemoryMultipartSource(val source: Source, override val size: DataSize) : MultipartSource {

    constructor(bytes: ByteArray) : this(Buffer().apply { write(bytes) }, bytes.size.bytes)

    constructor(content: String) : this(content.encodeToByteArray())

    override fun source(): Source {
        return source.peek()
    }

    override fun source(part: Part): Source {
        return part.pickSource(source)
    }

}
