package live.lingting.framework.util

import kotlinx.io.files.Path

/**
 * @author lingting 2026/3/3 15:14
 */
expect abstract class ExpectPlatformSystem() {

    val tmpDir: Path

    val homeDir: Path

    val workDir: Path

    fun getEnv(key: String): String?

}

object SystemUtils : ExpectPlatformSystem() {

    val tmpDirLingting = Path(tmpDir, ".lingting")

    val homeDirLingting = Path(homeDir, ".lingting")

}
