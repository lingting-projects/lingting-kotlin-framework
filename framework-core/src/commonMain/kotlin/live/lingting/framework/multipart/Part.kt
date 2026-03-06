package live.lingting.framework.multipart

import kotlinx.io.RawSink
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray


/**
 * 分片详情
 * 字节范围为全包. 从第 [start] 位到第 [end] 个字节
 * @author lingting 2024-09-05 14:47
 */
data class Part(
    val index: Long,
    val start: live.lingting.framework.data.DataSize,
    val end: live.lingting.framework.data.DataSize
) {

    val size = end - start + 1

    /**
     * 获取当前分片的字节数组
     */
    fun readBytes(path: Path): ByteArray {
        return openSource(path).use { it.readByteArray() }
    }

    /**
     * 获取流
     */
    fun openSource(path: Path): Source {
        val s = SystemFileSystem.source(path).buffered()
        s.skip(start.bytes)
        return _root_ide_package_.live.lingting.framework.io.LimitedSource(s, size.bytes).buffered()
    }

    /**
     * 选中流里面需要的部分
     */
    fun pickSource(source: Source): Source {
        val peek = source.peek()
        peek.skip(start.bytes)
        return _root_ide_package_.live.lingting.framework.io.LimitedSource(peek, size.bytes).buffered()
    }

    fun wrapperSink(sink: RawSink): Sink {
        return _root_ide_package_.live.lingting.framework.io.LimitedSink(sink, size.bytes).buffered()
    }

}
