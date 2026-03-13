package live.lingting.framework.util

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

internal actual val intervalIoContext: CoroutineContext
    get() = Dispatchers.Default
