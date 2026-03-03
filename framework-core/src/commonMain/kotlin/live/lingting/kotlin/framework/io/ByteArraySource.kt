package live.lingting.kotlin.framework.io

import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlin.math.min

/**
 * 零拷贝包装 ByteArray 为 RawSource
 * @param data 原始数组引用
 * @param offset 初始读取偏移量
 * @param length 限制读取长度（-1 表示读取到数组末尾）
 */
class ByteArraySource(
    private val data: ByteArray,
    offset: Int = 0,
    length: Int = -1
) : RawSource {

    // 当前绝对位置指针
    private var position = offset

    // 允许读取的最大绝对索引
    private val limit = if (length == -1) data.size else min(data.size, offset + length)

    private var closed = false

    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        check(!closed) { "Source is closed" }
        if (byteCount < 1) return 0

        if (position >= limit) return -1L

        val available = (limit - position).toLong()
        val toRead = min(available, byteCount).toInt()

        sink.write(data, position, toRead)

        position += toRead
        return toRead.toLong()
    }

    override fun close() {
        closed = true
    }

    /**
     * 实现逻辑上的快速跳过
     * @return 实际跳过的字节数
     */
    fun skip(byteCount: Long): Long {
        check(!closed) { "Source is closed" }
        if (byteCount <= 0) return 0L

        val available = (limit - position).toLong()
        val toSkip = min(available, byteCount).toInt()

        position += toSkip
        return toSkip.toLong()
    }

    /**
     * 获取当前剩余可读字节数
     */
    fun available(): Long = (limit - position).coerceAtLeast(0).toLong()

}
