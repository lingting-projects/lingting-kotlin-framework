package live.lingting.kotlin.framework.util

import kotlinx.io.Buffer
import kotlinx.io.writeString
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2025/12/24 15:21
 */
object BufferUtils {

    @JvmStatic
    fun Buffer.write(s: Char) {
        writeChar(s)
    }

    @JvmStatic
    fun Buffer.writeChar(s: Char) {
        write(s.toString())
    }

    @JvmStatic
    fun Buffer.write(s: String) {
        writeString(s)
    }

}
