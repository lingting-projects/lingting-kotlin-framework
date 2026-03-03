package live.lingting.kotlin.framework.util

import kotlinx.io.files.Path
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/3/3 15:21
 */
object FileUtils {

    @JvmStatic
    fun String.toPath() = Path(this)

}
