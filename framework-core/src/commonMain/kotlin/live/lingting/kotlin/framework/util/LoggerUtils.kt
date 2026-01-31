package live.lingting.kotlin.framework.util

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

typealias Logger = KLogger

/**
 * @author lingting 2026/1/30 17:20
 */
object LoggerUtils {

    @JvmStatic
    inline fun <reified T : Any> T.logger(): Logger {
        val cls = T::class
        return logger(cls)
    }

    @JvmStatic
    fun <T : Any> logger(cls: KClass<T>): Logger {
        return logger(cls.simpleName ?: "Root")
    }

    @JvmStatic
    fun logger(name: String): Logger {
        return KotlinLogging.logger(name)
    }

    inline val InlineLogger.log get() = logger()

    interface InlineLogger

}
