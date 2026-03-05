package live.lingting.kotlin.framework.io.multipart

import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.Sink
import kotlinx.io.buffered
import live.lingting.kotlin.framework.data.DataSize
import live.lingting.kotlin.framework.io.CompositeSource
import live.lingting.kotlin.framework.multipart.Part

/**
 * @author lingting 2026/2/26 15:30
 */
open class MemoryMultipartSink : MultipartSink {

    protected val lock = reentrantLock()

    protected val map = mutableMapOf<Part, Buffer>()

    override fun sink(part: Part): Sink {
        return lock.withLock {
            val buffer = map.getOrPut(part) { Buffer() }
            part.wrapperSink(buffer)
        }
    }

    override fun merge(): MultipartSource {
        val parts = lock.withLock {
            map.keys.sortedBy { it.index }
        }
        val list = mutableListOf<RawSource>()
        var size = DataSize.ZERO
        for (part in parts) {
            val buffer = map[part]!!
            val peek = buffer.peek()
            size += buffer.size
            list.add(peek)
        }

        val s = CompositeSource(list).buffered()
        return MemoryMultipartSource(s, size)
    }

    override fun flush() {
        //
    }

    override fun clear() {
        lock.withLock {
            map.values.forEach {
                it.close()
            }
            map.clear()
        }
    }

    override fun close() {
        clear()
    }

}
