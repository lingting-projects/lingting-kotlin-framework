package live.lingting.framework.util

import kotlinx.io.Buffer
import kotlinx.io.RawSource
import live.lingting.framework.io.ByteArraySource
import live.lingting.framework.util.DataSizeUtils.mb
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/3/3 15:43
 */
object IoUtils {

    @JvmStatic
    @JvmOverloads
    fun ByteArray.source(offset: Int = 0, length: Int = -1) = ByteArraySource(this, offset, length)

    /**
     * 不要用于转换网络流!
     */
    @JvmStatic
    @JvmOverloads
    fun RawSource.buffer(byteCount: Long = 1.mb.bytes): Buffer {
        val buffer = Buffer()
        while (true) {
            val len = readAtMostTo(buffer, byteCount)
            if (len < 0L) break
        }
        return buffer
    }

}
