package live.lingting.kotlin.framework.http

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.ProxyConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import kotlin.time.Duration
import io.ktor.client.HttpClient as KtorClient

/**
 * @author lingting 2026/1/31 16:39
 */
object HttpClient {

    @JvmField
    var buildDefault: () -> KtorClient = { Builder().build() }

    @JvmStatic
    fun default(): KtorClient = buildDefault()

    @Suppress("UNCHECKED_CAST")
    open class Builder {

        /**
         * 自动跟随重定向
         */
        protected var followRedirect: Boolean = true

        protected var callTimeout: Duration? = null

        protected var connectTimeout: Duration? = null

        protected var readTimeout: Duration? = null

        protected var writeTimeout: Duration? = null

        protected var proxy: ProxyConfig? = null

        fun followRedirect(v: Boolean): Builder {
            this.followRedirect = v
            return this
        }

        fun callTimeout(v: Duration?): Builder {
            this.callTimeout = v
            return this
        }

        fun connectTimeout(v: Duration?): Builder {
            this.connectTimeout = v
            return this
        }

        fun readTimeout(v: Duration?): Builder {
            this.readTimeout = v
            return this
        }

        fun writeTimeout(v: Duration?): Builder {
            this.writeTimeout = v
            return this
        }

        fun proxy(v: ProxyConfig?): Builder {
            this.proxy = v
            return this
        }

        open fun build(): KtorClient {
            return build { }
        }

        open fun build(consumer: (HttpClientConfig<CIOEngineConfig>) -> Unit): KtorClient {
            return KtorClient(CIO) {
                config(this)
                consumer(this)
            }
        }

        open fun config(config: HttpClientConfig<CIOEngineConfig>) {
            config.apply {
                if (followRedirect) {
                    install(HttpRedirect) {

                    }
                }

                if (callTimeout != null
                    || connectTimeout != null
                    || readTimeout != null
                    || writeTimeout != null
                ) {
                    install(HttpTimeout) {
                        connectTimeoutMillis = connectTimeout?.inWholeMilliseconds
                        socketTimeoutMillis = readTimeout?.inWholeMilliseconds
                        if (writeTimeout != null) {
                            socketTimeoutMillis = if (socketTimeoutMillis == null) {
                                writeTimeout?.inWholeMilliseconds
                            } else {
                                socketTimeoutMillis!! + writeTimeout!!.inWholeMilliseconds
                            }
                        }
                    }
                }

                engine {
                    this.proxy = this@Builder.proxy
                }
            }
        }

    }

}
