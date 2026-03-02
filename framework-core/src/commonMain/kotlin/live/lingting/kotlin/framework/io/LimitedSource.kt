package live.lingting.kotlin.framework.io

import kotlinx.io.Buffer
import kotlinx.io.RawSource

/**
 * @author lingting 2026/2/13 17:35
 */
class LimitedSource(
    private val delegate: RawSource,
    /**
     * 限制读取的字节长度
     */
    private val limit: Long
) : RawSource {

    private var readLength = 0L

    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        if (byteCount < 1) return 0
        val remaining = limit - readLength
        if (remaining <= 0) return -1

        val toRead = minOf(byteCount, remaining)
        val read = delegate.readAtMostTo(sink, toRead)

        if (read != -1L) {
            readLength += read
        }
        // 源读完了
        else {
            readLength = limit
        }
        return read
    }

    override fun close() = delegate.close()

}
