package live.lingting.framework.util

import kotlinx.io.files.Path
import live.lingting.framework.util.FileUtils.toPath

actual abstract class ExpectPlatformSystem actual constructor() {

    // 浏览器没有临时目录，通常指向根目录或 IndexedDB 虚拟路径
    actual val tmpDir: Path = "/tmp".toPath()

    // 浏览器没有主目录概念，通常返回当前 Origin 的根
    actual val homeDir: Path = "/".toPath()

    // 浏览器没有工作路径的说法
    actual val workDir: Path = "/".toPath()

    /**
     * 浏览器没有系统环境变量。
     */
    actual fun getEnv(key: String): String? = null

}

@OptIn(ExperimentalWasmJsInterop::class)
fun isNode(): Boolean =
    js("typeof process !== 'undefined' && process.versions != null && process.versions.node != null")

inline val SystemUtils.isBrowser: Boolean get() = !isNode

inline val SystemUtils.isNode: Boolean get() = isNode()
