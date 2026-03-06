package live.lingting.framework.webserver.util

import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.ApplicationEngineFactory
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import live.lingting.framework.webserver.WebServer
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/3/6 15:48
 */
object WebServerUtils {

    @JvmStatic
    fun CoroutineScope.webserver(
        factory: ApplicationEngineFactory<CIOApplicationEngine, CIOApplicationEngine.Configuration> = CIO,
        port: Int = 0,
        host: String = "0.0.0.0",
        watchPaths: List<String> = listOf(SystemFileSystem.resolve(Path(".")).toString()),
        parentCoroutineContext: CoroutineContext = EmptyCoroutineContext,
        module: suspend Application.() -> Unit
    ): WebServer {
        val server = embeddedServer(
            factory = factory,
            host = host,
            port = port,
            watchPaths = watchPaths,
            parentCoroutineContext = parentCoroutineContext,
            module = module
        )
        return WebServer(server)
    }

    @JvmStatic
    suspend fun <E : ApplicationEngine, C : ApplicationEngine.Configuration> EmbeddedServer<E, C>.ports(): List<Int> {
        return engine.resolvedConnectors().map { it.port }
    }

}
