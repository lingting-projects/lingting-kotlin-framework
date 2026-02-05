package live.lingting.kotlin.framework.http.util

import io.ktor.http.DEFAULT_PORT
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.fullPath
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/4 19:30
 */
object HttpUrlUtils {

    @JvmStatic
    fun URLBuilder.isHttps(): Boolean = protocolOrNull?.name == URLProtocol.HTTPS.name

    @JvmStatic
    fun URLBuilder.headerHost(): String {
        appendPathSegments()
        val h = host
        require(h.isNotBlank()) { "Host [$host] is invalid!" }
        val p = if (port == DEFAULT_PORT) protocolOrNull?.defaultPort else port
        require(p == null || (p in 1..<65535)) { "Port [$p] is invalid!" }

        val https = isHttps()
        if (https && p == 443) {
            return h
        } else if (!https && p == 80) {
            return h
        }

        return "$h:$p"
    }

    @JvmStatic
    fun URLBuilder.buildPath(): String {
        return build().fullPath
    }

    @JvmStatic
    fun URLBuilder.buildBySort(): Url {
        val builder = URLBuilder(this)
        builder.parameters.clear()
        parameters.names().sorted().forEach { name ->
            val all = parameters.getAll(name)
            if (all != null) {
                builder.parameters.appendAll(name, all)
            }
        }
        return builder.build()
    }

    @JvmStatic
    fun URLBuilder.buildStringBySort(): String {
        val url = buildBySort()
        return url.toString()
    }

}
