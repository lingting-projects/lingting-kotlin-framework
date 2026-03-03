package live.lingting.kotlin.framework.util

import live.lingting.kotlin.framework.io.ByteArraySource
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/3/3 15:43
 */
object IoUtils {

    @JvmStatic
    @JvmOverloads
    fun ByteArray.source(offset: Int = 0, length: Int = -1) = ByteArraySource(this, offset, length)

}
