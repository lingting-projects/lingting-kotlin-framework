package live.lingting.kotlin.framework.io

import kotlinx.io.Buffer
import kotlinx.io.IOException
import kotlinx.io.RawSink

class LimitedSink(
    private val delegate: RawSink,
    private val limit: Long
) : RawSink {
    private var written = 0L

    override fun write(source: Buffer, byteCount: Long) {
        val remaining = limit - written
        if (remaining <= 0) throw IOException("写入内容已超过上限!")

        val toWrite = minOf(byteCount, remaining)
        delegate.write(source, toWrite)
        written += toWrite
    }

    override fun flush() = delegate.flush()

    override fun close() = delegate.close()

}
