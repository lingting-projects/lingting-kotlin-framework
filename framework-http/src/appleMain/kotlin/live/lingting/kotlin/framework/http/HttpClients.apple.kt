package live.lingting.kotlin.framework.http

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIOEngineConfig

internal actual fun HttpClients.Builder.internalDisableSsl(config: HttpClientConfig<CIOEngineConfig>) {
}
