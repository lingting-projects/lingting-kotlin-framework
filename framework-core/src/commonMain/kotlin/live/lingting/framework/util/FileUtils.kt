package live.lingting.framework.util

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import live.lingting.framework.util.DataSizeUtils.bytes
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/3/3 15:21
 */
object FileUtils {

    @JvmStatic
    fun String.toPath() = Path(this)

    @JvmStatic
    fun Path.size() = SystemFileSystem.metadataOrNull(this)?.size?.bytes

}
