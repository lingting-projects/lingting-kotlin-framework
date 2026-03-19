package live.lingting.framework.util

import kotlinx.coroutines.CancellationException
import live.lingting.framework.exception.TimeoutException
import live.lingting.framework.value.WaitValue
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.time.Duration

/**
 * @author lingting 2026/3/19 10:20
 */
object WaitValueUtils {

    /**
     * @param block 值为true之后执行
     */
    @JvmStatic
    @JvmOverloads
    @Throws(TimeoutException::class, CancellationException::class)
    suspend fun <V : Boolean?> WaitValue<V>.waitTrue(duration: Duration? = null, block: (suspend () -> Unit)? = null) {
        val await = await(duration) { it == true }
        if (await == true && block != null) {
            block()
        }
    }

    /**
     * @param block 值为false之后执行
     */
    @JvmStatic
    @JvmOverloads
    @Throws(TimeoutException::class, CancellationException::class)
    suspend fun <V : Boolean?> WaitValue<V>.waitFalse(duration: Duration? = null, block: (suspend () -> Unit)? = null) {
        val await = await(duration) { it == false }
        if (await == false && block != null) {
            block()
        }
    }

}
