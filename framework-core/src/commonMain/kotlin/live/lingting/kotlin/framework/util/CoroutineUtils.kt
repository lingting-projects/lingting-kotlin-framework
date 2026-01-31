package live.lingting.kotlin.framework.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlin.jvm.JvmField

object CoroutineUtils {

    @JvmField
    open var defaultScope: CoroutineScope = GlobalScope

}
