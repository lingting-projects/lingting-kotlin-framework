package live.lingting.framework.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

object CoroutineUtils {

    var defaultScope: CoroutineScope = GlobalScope
        private set

    @JvmStatic
    @JvmOverloads
    fun switchScope(scope: CoroutineScope, cancelOld: Boolean = false) {
        if (scope == defaultScope) {
            return
        }
        val old = defaultScope
        defaultScope = scope
        if (cancelOld) {
            old.cancel("切换scope时取消任务!")
        }
    }

    @JvmStatic
    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = defaultScope.launch(context, start, block)

    @JvmStatic
    fun <T> async(
        context: CoroutineContext,
        start: CoroutineStart,
        block: suspend CoroutineScope.() -> T
    ) = defaultScope.async(context, start, block)

}
