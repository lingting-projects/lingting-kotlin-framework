package live.lingting.framework.async

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2026/2/25 15:34
 */
open class CoroutineAsync : live.lingting.framework.async.AbstractAsync {

    private val scope: CoroutineScope

    @JvmOverloads
    constructor(
        limit: Long = _root_ide_package_.live.lingting.framework.async.Async.UNLIMITED,
        scope: CoroutineScope = _root_ide_package_.live.lingting.framework.util.CoroutineUtils.defaultScope
    ) : super(limit) {
        this.scope = scope
    }

    override fun run(item: live.lingting.framework.async.AsyncItem) {
        scope.launch {
            item.block(item)
        }
    }

}

@JvmOverloads
fun async(
    limit: Long = _root_ide_package_.live.lingting.framework.async.Async.UNLIMITED,
    scope: CoroutineScope = _root_ide_package_.live.lingting.framework.util.CoroutineUtils.defaultScope
) =
    _root_ide_package_.live.lingting.framework.async.CoroutineAsync(limit, scope)
