package live.lingting.framework.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal actual val intervalIoContext: kotlin.coroutines.CoroutineContext
    get() = Dispatchers.IO
