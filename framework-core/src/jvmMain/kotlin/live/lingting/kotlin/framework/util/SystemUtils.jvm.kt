package live.lingting.kotlin.framework.util

import kotlinx.io.files.Path
import live.lingting.kotlin.framework.util.FileUtils.toPath

actual abstract class ExpectPlatformSystem actual constructor() {

    actual val tmpDir: Path = System.getProperty("java.io.tmpdir").toPath()

    actual val homeDir: Path = System.getProperty("user.home").toPath()

    actual val workDir: Path = System.getProperty("user.dir").toPath()

    actual fun getEnv(key: String): String? = System.getenv(key)

}
