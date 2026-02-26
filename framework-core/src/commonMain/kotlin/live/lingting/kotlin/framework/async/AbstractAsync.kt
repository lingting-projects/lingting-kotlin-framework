package live.lingting.kotlin.framework.async

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import live.lingting.kotlin.framework.util.LoggerUtils.logger

/**
 * @author lingting 2026/2/26 10:35
 */
abstract class AbstractAsync : Async {

    protected val log = logger()

    protected val lock = reentrantLock()

    protected val items = HashMap<String, AsyncItem>()

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

    override fun submit(name: String?, block: suspend (AsyncItem) -> Unit) {
        lock.withLock {
            val item = AsyncItem(name, { execBlock(block) })
            items[item.id] = item
            waitQueue.addLast(item.id)
            allRef.incrementAndGet()
            walk()
        }
    }

    protected open suspend fun AsyncItem.execBlock(block: suspend (AsyncItem) -> Unit) {
        toRun()
        try {
            if (!isFinish) {
                block(this)
                toSuccess()
            }
        } catch (t: Throwable) {
            log.error(t) { "异步任务执行异常!" }
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

    protected abstract fun run(item: AsyncItem)

    override fun cancelAll() {
        lock.withLock {
            items.values.forEach {
                it.toAbort()
            }
        }
    }

}
