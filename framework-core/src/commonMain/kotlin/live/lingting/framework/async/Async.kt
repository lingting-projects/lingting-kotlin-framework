package live.lingting.framework.async

import kotlin.time.Duration

/**
 * @author lingting 2026/2/25 15:16
 */
interface Async {

    companion object {

        const val UNLIMITED: Long = -1

    }

    val limit: Long

    /**
     * 运行中的和队列中待执行的
     */
    val notCompleted: Long
        get() = all - completed

    val running: Long

    val completed: Long

    val all: Long

    /**
     * 是否可以无限制执行
     */
    val isUnlimited: Boolean
        get() = limit < 0

    /**
     * 是否已满, 不能立即执行新任务
     */
    val isFull: Boolean
        get() = !isUnlimited && running >= limit

    fun submit(block: suspend (live.lingting.framework.async.AsyncItem) -> Unit) = submit(null, block)

    fun submit(name: String?, block: suspend (live.lingting.framework.async.AsyncItem) -> Unit)

    @Throws(_root_ide_package_.live.lingting.framework.exception.TimeoutException::class)
    suspend fun await() = await(null)

    /**
     * 等待结束
     * @param duration       超时时间
     */
    @Throws(_root_ide_package_.live.lingting.framework.exception.TimeoutException::class)
    suspend fun await(duration: Duration? = null) {
        _root_ide_package_.live.lingting.framework.concurrent.Await.waitTrue(duration) { running == 0L }
    }

    @Throws(_root_ide_package_.live.lingting.framework.exception.TimeoutException::class)
    suspend fun awaitIdle(duration: Duration? = null) {
        if (!isUnlimited) {
            _root_ide_package_.live.lingting.framework.concurrent.Await.waitTrue(duration) { notCompleted == 0L }
        }
    }

    fun cancelAll()

}
