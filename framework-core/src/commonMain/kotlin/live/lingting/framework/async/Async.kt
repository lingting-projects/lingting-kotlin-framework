package live.lingting.framework.async

import kotlinx.coroutines.CancellationException
import live.lingting.framework.concurrent.Await
import live.lingting.framework.exception.TimeoutException
import kotlin.jvm.JvmField
import kotlin.time.Duration

/**
 * @author lingting 2026/2/25 15:16
 */
interface Async {

    companion object {

        @JvmField
        val UNLIMITED: Long = -1

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

    fun submit(block: suspend (AsyncItem) -> Unit) = submit(null, block)

    fun submit(name: String?, block: suspend (AsyncItem) -> Unit)

    @Throws(TimeoutException::class, CancellationException::class)
    suspend fun await() = await(null)

    /**
     * 等待结束
     * @param duration       超时时间
     */
    @Throws(TimeoutException::class, CancellationException::class)
    suspend fun await(duration: Duration? = null) {
        Await.waitTrue(duration) { running == 0L }
    }

    @Throws(TimeoutException::class, CancellationException::class)
    suspend fun awaitIdle(duration: Duration? = null) {
        if (!isUnlimited) {
            Await.waitTrue(duration) { notCompleted == 0L }
        }
    }

    fun cancelAll()

}
