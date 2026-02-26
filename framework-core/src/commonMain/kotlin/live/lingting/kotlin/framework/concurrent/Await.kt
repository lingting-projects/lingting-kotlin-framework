package live.lingting.kotlin.framework.concurrent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import live.lingting.kotlin.framework.concurrent.Awaiter.Companion.defaultContext
import live.lingting.kotlin.framework.concurrent.Awaiter.Companion.defaultDelay
import live.lingting.kotlin.framework.concurrent.Awaiter.Companion.defaultScope
import live.lingting.kotlin.framework.concurrent.Awaiter.Companion.defaultStart
import live.lingting.kotlin.framework.exception.TimeoutException
import live.lingting.kotlin.framework.util.ValueUtils
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.time.Duration

/**
 * @author lingting 2026/2/25 19:05
 */
object Await {

    @JvmStatic
    suspend fun wait(duration: Duration) {
        delay(duration)
    }

    @JvmStatic
    @JvmOverloads
    @Throws(TimeoutException::class)
    suspend fun waitTrue(
        timeout: Duration?,
        name: String? = null,
        delay: suspend () -> Unit = defaultDelay,
        scope: CoroutineScope = defaultScope,
        context: CoroutineContext = defaultContext,
        start: CoroutineStart = defaultStart,
        worker: suspend CoroutineScope.() -> Boolean?
    ) {
        val awaiter = Awaiter(
            timeout = timeout,
            name = name,
            delay = delay,
            scope = scope,
            context = context,
            start = start,
            predicate = { it == true },
            worker = worker
        )
        awaiter.await()
    }

    @JvmStatic
    @JvmOverloads
    @Throws(TimeoutException::class)
    suspend fun waitFalse(
        timeout: Duration?,
        name: String? = null,
        delay: suspend () -> Unit = defaultDelay,
        scope: CoroutineScope = defaultScope,
        context: CoroutineContext = defaultContext,
        start: CoroutineStart = defaultStart,
        worker: suspend CoroutineScope.() -> Boolean?
    ) {
        val awaiter = Awaiter(
            timeout = timeout,
            name = name,
            delay = delay,
            scope = scope,
            context = context,
            start = start,
            predicate = { it == false },
            worker = worker
        )
        awaiter.await()
    }

    @JvmStatic
    @JvmOverloads
    @Throws(TimeoutException::class)
    suspend fun <T> waitNull(
        timeout: Duration?,
        name: String? = null,
        delay: suspend () -> Unit = defaultDelay,
        scope: CoroutineScope = defaultScope,
        context: CoroutineContext = defaultContext,
        start: CoroutineStart = defaultStart,
        worker: suspend CoroutineScope.() -> T?
    ) {
        val awaiter = Awaiter(
            timeout = timeout,
            name = name,
            delay = delay,
            scope = scope,
            context = context,
            start = start,
            predicate = { it == null },
            worker = worker
        )
        awaiter.await()
    }

    @JvmStatic
    @JvmOverloads
    @Throws(TimeoutException::class)
    suspend fun <T> waitNotNull(
        timeout: Duration?,
        name: String? = null,
        delay: suspend () -> Unit = defaultDelay,
        scope: CoroutineScope = defaultScope,
        context: CoroutineContext = defaultContext,
        start: CoroutineStart = defaultStart,
        worker: suspend CoroutineScope.() -> T?
    ): T {
        val awaiter = Awaiter(
            timeout = timeout,
            name = name,
            delay = delay,
            scope = scope,
            context = context,
            start = start,
            predicate = { it != null },
            worker = worker
        )
        return awaiter.await()!!
    }

    @JvmStatic
    @JvmOverloads
    @Throws(TimeoutException::class)
    suspend fun <T> waitPresent(
        timeout: Duration?,
        name: String? = null,
        delay: suspend () -> Unit = defaultDelay,
        scope: CoroutineScope = defaultScope,
        context: CoroutineContext = defaultContext,
        start: CoroutineStart = defaultStart,
        worker: suspend CoroutineScope.() -> T?
    ): T {
        val awaiter = Awaiter(
            timeout = timeout,
            name = name,
            delay = delay,
            scope = scope,
            context = context,
            start = start,
            predicate = { ValueUtils.isPresent(it) },
            worker = worker
        )
        return awaiter.await()!!
    }

    @JvmStatic
    @JvmOverloads
    @Throws(TimeoutException::class)
    suspend fun <T> wait(
        timeout: Duration?,
        name: String? = null,
        delay: suspend () -> Unit = defaultDelay,
        scope: CoroutineScope = defaultScope,
        context: CoroutineContext = defaultContext,
        start: CoroutineStart = defaultStart,
        predicate: suspend (T?) -> Boolean,
        worker: suspend CoroutineScope.() -> T?
    ): T? {
        val awaiter = Awaiter(
            timeout = timeout,
            name = name,
            delay = delay,
            scope = scope,
            context = context,
            start = start,
            predicate = predicate,
            worker = worker
        )
        return awaiter.await()
    }

}
