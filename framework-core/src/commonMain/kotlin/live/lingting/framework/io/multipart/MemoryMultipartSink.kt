package live.lingting.framework.io.multipart

import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.Sink
import kotlinx.io.buffered

/**
 * @author lingting 2026/2/26 15:30
 */
open class MemoryMultipartSink : live.lingting.framework.io.multipart.MultipartSink {

    protected val lock = reentrantLock()

    protected val map = mutableMapOf<live.lingting.framework.multipart.Part, Buffer>()

    override fun sink(part: live.lingting.framework.multipart.Part): Sink {
        return lock.withLock {
            val buffer = map.getOrPut(part) { Buffer() }
            part.wrapperSink(buffer)
        }
    }

    override fun merge(): live.lingting.framework.io.multipart.MultipartSource {
        val parts = lock.withLock {
            map.keys.sortedBy { it.index }
        }
        val list = mutableListOf<RawSource>()
        var size = _root_ide_package_.live.lingting.framework.data.DataSize.ZERO
        for (part in parts) {
            val buffer = map[part]!!
            val peek = buffer.peek()
            size += buffer.size
            list.add(peek)
        }

        val s = _root_ide_package_.live.lingting.framework.io.CompositeSource(list).buffered()
        return _root_ide_package_.live.lingting.framework.io.multipart.MemoryMultipartSource(s, size)
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
