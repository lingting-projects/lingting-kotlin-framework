package live.lingting.kotlin.framework.async

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import live.lingting.kotlin.framework.util.CoroutineUtils
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2026/2/25 15:34
 */
class CoroutineAsync : AbstractAsync {

    override val limit: Long

    private val scope: CoroutineScope

    @JvmOverloads
    constructor(limit: Long = Async.UNLIMITED, scope: CoroutineScope = CoroutineUtils.defaultScope) : super() {
        this.limit = limit
        this.scope = scope
    }

    override fun run(item: AsyncItem) {
        scope.launch {
            item.block(item)
        }
    }

}

@JvmOverloads
fun async(limit: Long = Async.UNLIMITED, scope: CoroutineScope = CoroutineUtils.defaultScope) =
    CoroutineAsync(limit, scope)
