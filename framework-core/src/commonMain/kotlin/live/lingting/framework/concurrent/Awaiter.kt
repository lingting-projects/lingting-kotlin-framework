package live.lingting.framework.concurrent

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmField
import kotlin.time.Duration

/**
 * @author lingting 2026/2/25 16:16
 */
open class Awaiter<R>(
    val timeout: Duration?,
    val name: String? = null,
    protected val delay: suspend () -> Unit = defaultDelay,
    protected val scope: CoroutineScope = defaultScope,
    protected val context: CoroutineContext = defaultContext,
    protected val start: CoroutineStart = defaultStart,
    protected val predicate: suspend (R?) -> Boolean,
    protected val worker: suspend CoroutineScope.() -> R?
) {

    companion object {

        @JvmField
        var defaultDelay: suspend () -> Unit = { delay(100) }

        @JvmField
        var defaultScope: CoroutineScope = _root_ide_package_.live.lingting.framework.util.CoroutineUtils.defaultScope

        @JvmField
        var defaultContext: CoroutineContext = EmptyCoroutineContext

        @JvmField
        var defaultStart: CoroutineStart = CoroutineStart.DEFAULT

    }

    @Throws(_root_ide_package_.live.lingting.framework.exception.TimeoutException::class)
    suspend fun await(): R? {
        val combinedContext = if (!name.isNullOrBlank()) context + CoroutineName(name) else context

        val deferred = scope.async(combinedContext, start) {
            if (timeout != null && timeout.isPositive()) {
                try {
                    withTimeout(timeout) { poll() }
                } catch (_: kotlinx.coroutines.TimeoutCancellationException) {
                    throw _root_ide_package_.live.lingting.framework.exception.TimeoutException("等待超时! 预计时间: $timeout")
                }
            } else {
                poll()
            }
        }

        // 等待结果。如果 deferred 抛出异常，await() 会直接抛出该异常
        return try {
            deferred.await()
        } catch (e: CancellationException) {
            // 如果外部调用 await() 的协程被取消，同步取消掉 scope 里的任务
            deferred.cancel()
            throw e
        }
    }

    private suspend fun CoroutineScope.poll(): R? {
        while (isActive) {
            // worker() 抛出的异常会自动让 async 失败
            val r = worker()
            if (predicate(r)) {
                return r
            }
            delay()
        }
        throw IllegalStateException("等待任务协程被终止!")
    }

}
