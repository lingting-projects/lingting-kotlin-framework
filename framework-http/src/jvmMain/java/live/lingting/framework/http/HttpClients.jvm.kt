package live.lingting.framework.http

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIOEngineConfig

internal actual fun HttpClients.Builder.internalDisableSsl(config: HttpClientConfig<CIOEngineConfig>) {
    config.engine {
        https {
            trustManager = Https.SSL_DISABLED_TRUST_MANAGER
        }
    }
}
