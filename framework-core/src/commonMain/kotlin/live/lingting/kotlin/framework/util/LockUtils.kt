package live.lingting.kotlin.framework.util

import kotlinx.atomicfu.locks.ReentrantLock
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/26 14:25
 */
object LockUtils {

    @JvmStatic
    @JvmOverloads
    fun ReentrantLock.withTryLock(block: () -> Unit, onLockFailed: (() -> Unit)? = null) {
        if (!tryLock()) {
            onLockFailed?.run { this() }
            return
        }
        try {
            block()
        } finally {
            unlock()
        }
    }

}
