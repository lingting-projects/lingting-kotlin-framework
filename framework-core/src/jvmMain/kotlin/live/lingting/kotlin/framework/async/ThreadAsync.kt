package live.lingting.kotlin.framework.async

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executor

/**
 * @author lingting 2026/2/26 14:16
 */
open class ThreadAsync : CoroutineAsync {

    @JvmOverloads
    constructor(limit: Long = Async.UNLIMITED, executor: Executor) : super(
        limit,
        CoroutineScope(executor.asCoroutineDispatcher() + SupervisorJob())
    )

}
