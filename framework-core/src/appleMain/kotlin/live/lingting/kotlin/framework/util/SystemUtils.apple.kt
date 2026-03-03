package live.lingting.kotlin.framework.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.io.files.Path
import live.lingting.kotlin.framework.util.FileUtils.toPath
import platform.Foundation.NSBundle
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSTemporaryDirectory
import platform.posix.getenv

actual abstract class ExpectPlatformSystem actual constructor() {

    actual val tmpDir: Path = NSTemporaryDirectory().toPath()

    actual val homeDir: Path = NSHomeDirectory().toPath()

    actual val workDir: Path = NSBundle.mainBundle.bundlePath.toPath()

    @OptIn(ExperimentalForeignApi::class)
    actual fun getEnv(key: String): String? = getenv(key)?.toKString()

}
