package live.lingting.framework.webserver

import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import live.lingting.framework.webserver.util.WebServerUtils.ports

/**
 * @author lingting 2026/3/6 15:42
 */
open class WebServer(val instance: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>) {

    fun start(wait: Boolean = false) {
        instance.start(wait)
    }

    suspend fun startSuspend(wait: Boolean = false) {
        instance.startSuspend(wait)
    }

    fun stop(
        gracePeriodMillis: Long = instance.engineConfig.shutdownGracePeriod,
        timeoutMillis: Long = instance.engineConfig.shutdownTimeout
    ) {
        instance.stop(gracePeriodMillis, timeoutMillis)
    }

    suspend fun stopSuspend(
        gracePeriodMillis: Long = instance.engineConfig.shutdownGracePeriod,
        timeoutMillis: Long = instance.engineConfig.shutdownTimeout
    ) {
        instance.stopSuspend(gracePeriodMillis, timeoutMillis)
    }

    suspend fun port() = instance.ports().first()

}
