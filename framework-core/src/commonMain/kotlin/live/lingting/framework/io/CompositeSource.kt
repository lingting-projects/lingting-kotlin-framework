package live.lingting.framework.io

import kotlinx.io.Buffer
import kotlinx.io.RawSource

/**
 * 基于多个源的零拷贝合并读取方式
 * @author lingting 2026/2/26 16:39
 */
class CompositeSource(private val list: List<RawSource>) : RawSource {

    private var currentIndex = 0
    private val totalParts = list.size

    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
        if (byteCount < 1) return 0
        while (currentIndex < totalParts) {
            val current = list[currentIndex]
            val read = current.readAtMostTo(sink, byteCount)

            if (read != -1L) {
                // 读到了数据，直接返回
                return read
            }

            // 当前分片读完了，切换到下一个
            currentIndex++
        }

        return -1
    }

    override fun close() {
        list.forEach {
            it.close()
        }
    }

}
