package live.lingting.kotlin.framework.value

import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import live.lingting.kotlin.framework.exception.TimeoutException
import live.lingting.kotlin.framework.util.ValueUtils
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.time.Duration

/**
 * @author lingting 2026/1/30 16:11
 */
open class WaitValue<T> @JvmOverloads constructor(v: T? = null) {

    companion object {

        @JvmStatic
        fun <T> of(): WaitValue<T> {
            return WaitValue()
        }

        @JvmStatic
        fun <T> of(t: T?): WaitValue<T> {
            return WaitValue(t)
        }

    }

    protected val lock = reentrantLock()

    /**
     * MutableSharedFlow 会在赋值相同值时触发, 可以避免有些值在时间流动下从不满足到满足等待条件
     */
    protected val state = MutableSharedFlow<T?>(
        // 保证新加入的 wait 能立即拿到当前最新值
        replay = 1,
        // DROP_OLDEST: 确保发射（tryEmit）永远不会挂起
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    ).apply { tryEmit(v) }

    var value: T?
        get() = state.replayCache.firstOrNull()
        set(value) {
            state.tryEmit(value)
        }

    val isNull: Boolean
        get() = value == null

    fun <R> withLock(block: () -> R): R {
        return lock.withLock(block)
    }

    fun update(t: T?) {
        compute { t }
    }

    /**
     * 进行运算, 同时仅允许一个线程获取
     * @param operator 运行行为
     */
    fun compute(operator: (T?) -> T?): T? {
        return withLock {
            val v = value
            val nv = operator(v)
            value = nv
            nv
        }
    }

    /**
     * 消费当前值
     */
    fun consumer(consumer: (T?) -> Unit) {
        consumer(value)
    }

    @JvmOverloads
    @Throws(TimeoutException::class)
    suspend fun notNull(duration: Duration? = null): T {
        val t = await(duration) { it != null }
        return t!!
    }

    @JvmOverloads
    @Throws(TimeoutException::class)
    suspend fun notEmpty(duration: Duration? = null): T {
        val t = await(duration) { ValueUtils.isPresent(it) }
        return t!!
    }

    @JvmOverloads
    @Throws(TimeoutException::class)
    suspend fun await(duration: Duration? = null, predicate: (T?) -> Boolean): T? {
        val v = value
        if (predicate(v)) {
            return v
        }

        val flow = state.filter { predicate(it) }
        if (duration == null || duration.isInfinite() || !duration.isPositive()) {
            return flow.first()
        }

        try {
            return withTimeout(duration) {
                flow.first()
            }
        } catch (_: TimeoutCancellationException) {
            throw TimeoutException("Wait timeout after $duration")
        }
    }

}
