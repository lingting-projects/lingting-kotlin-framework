package live.lingting.framework.io.multipart

import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.io.RawSource
import kotlinx.io.Sink
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import live.lingting.framework.util.FileUtils.size

/**
 * @author lingting 2026/2/26 16:57
 */
open class FileMultipartSink(val path: Path) : live.lingting.framework.io.multipart.MultipartSink {

    /**
     * 临时文件存储的文件夹
     */
    protected val dir by lazy {
        val parent = path.parent!!
        val dir = Path(parent, ".parts_${path.name}")
        SystemFileSystem.createDirectories(dir)
        dir
    }

    protected val lock = reentrantLock()

    protected val map = mutableMapOf<live.lingting.framework.multipart.Part, Path>()

    override fun sink(part: live.lingting.framework.multipart.Part): Sink {
        return lock.withLock {
            val path = map.getOrPut(part) { Path(dir, "${part.index}.part") }
            val sink = SystemFileSystem.sink(path)
            part.wrapperSink(sink)
        }
    }

    override fun merge(): live.lingting.framework.io.multipart.MultipartSource {
        val parts = lock.withLock {
            map.keys.sortedBy { it.index }
        }
        val list = mutableListOf<RawSource>()
        var size = _root_ide_package_.live.lingting.framework.data.DataSize.ZERO
        for (part in parts) {
            val path = map[part]!!
            val source = SystemFileSystem.source(path)
            size += path.size() ?: _root_ide_package_.live.lingting.framework.data.DataSize.ZERO
            list.add(source)
        }

        val s = _root_ide_package_.live.lingting.framework.io.CompositeSource(list).buffered()
        return _root_ide_package_.live.lingting.framework.io.multipart.MemoryMultipartSource(s, size)
    }

    override fun flush() {
        lock.withLock {
            val sink = SystemFileSystem.sink(path).buffered()
            sink.use {
                map.entries.sortedBy { it.key.index }
                    .forEach { (part, path) ->
                        val source = SystemFileSystem.source(path)
                        sink.write(source, part.size.bytes)
                    }
                sink.flush()
            }
        }
    }

    override fun clear() {
        lock.withLock {
            map.values.forEach {
                SystemFileSystem.delete(it)
            }
            SystemFileSystem.delete(dir)
        }
    }

    override fun close() {
        flush()
        clear()
    }

}
