package live.lingting.framework.async

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import live.lingting.framework.util.LoggerUtils.logger
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2026/2/26 10:35
 */
abstract class AbstractAsync : live.lingting.framework.async.Async {

    final override var limit: Long = _root_ide_package_.live.lingting.framework.async.Async.UNLIMITED
        private set

    @JvmOverloads
    constructor(limit: Long = _root_ide_package_.live.lingting.framework.async.Async.UNLIMITED) {
        this.limit = limit
    }

    protected val log = logger()

    protected val lock = reentrantLock()

    protected val items = HashMap<String, live.lingting.framework.async.AsyncItem>()

    protected val waitQueue = ArrayDeque<String>()

    protected val runningRef = atomic(0L)

    override val running: Long
        get() = runningRef.value

    protected val completedRef = atomic(0L)

    override val completed: Long
        get() = completedRef.value

    protected val allRef = atomic(0L)

    override val all: Long
        get() = allRef.value

    override fun submit(name: String?, block: suspend (live.lingting.framework.async.AsyncItem) -> Unit) {
        lock.withLock {
            val item = _root_ide_package_.live.lingting.framework.async.AsyncItem(name, { execBlock(block) })
            items[item.id] = item
            waitQueue.addLast(item.id)
            allRef.incrementAndGet()
            walk()
        }
    }

    protected open suspend fun live.lingting.framework.async.AsyncItem.execBlock(block: suspend (live.lingting.framework.async.AsyncItem) -> Unit) {
        toRun()
        try {
            if (!isFinish) {
                block(this)
                toSuccess()
            }
        } catch (t: Throwable) {
            log.error(t) { "[异步任务] 执行异常!" }
            toFail()
        } finally {
            lock.withLock {
                items.remove(id)
                runningRef.decrementAndGet()
                completedRef.incrementAndGet()
            }
            walk()
        }
    }

    protected open fun walk() {
        lock.withLock {
            while (!isFull && waitQueue.isNotEmpty()) {
                val id = waitQueue.removeFirst()
                val item = items[id]
                if (item != null) {
                    if (item.isFinish) {
                        completedRef.incrementAndGet()
                        items.remove(id)
                    } else {
                        runningRef.incrementAndGet()
                        run(item)
                    }
                }
            }
        }
    }

    protected abstract fun run(item: live.lingting.framework.async.AsyncItem)

    override fun cancelAll() {
        lock.withLock {
            items.values.forEach {
                it.toAbort()
            }
        }
    }

}
