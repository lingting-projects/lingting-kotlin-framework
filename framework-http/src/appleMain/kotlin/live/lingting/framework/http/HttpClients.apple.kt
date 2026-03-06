package live.lingting.framework.http

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIOEngineConfig

internal actual fun live.lingting.framework.http.HttpClients.Builder.internalDisableSsl(config: HttpClientConfig<CIOEngineConfig>) {
}
