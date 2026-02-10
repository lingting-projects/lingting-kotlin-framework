package live.lingting.kotlin.framework.http.util

import io.ktor.http.DEFAULT_PORT
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.fullPath
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import live.lingting.kotlin.framework.util.BufferUtils.writeChar
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

    /**
     * URL 编码 (Percent-Encoding)
     */
    @JvmStatic
    fun encode(str: String): String {
        val unreserved = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"
        val buffer = Buffer()
        val bytes = str.encodeToByteArray()
        val hexChars = "0123456789ABCDEF"

        for (b in bytes) {
            val i = b.toInt() and 0xFF
            val c = i.toChar()
            if (c in unreserved) {
                buffer.writeChar(c)
            } else {
                buffer.writeChar('%')
                // 也可以复用你的逻辑，但这里为了避免生成过多小对象，直接位移写入
                buffer.writeChar(hexChars[i shr 4])
                buffer.writeChar(hexChars[i and 0x0F])
            }
        }
        return buffer.readByteArray().decodeToString()
    }

    /**
     * URL 解码
     */
    @JvmStatic
    fun decode(str: String): String {
        val buffer = Buffer()
        var i = 0
        while (i < str.length) {
            when (val c = str[i]) {
                '%' if i + 2 < str.length -> {
                    val hex = str.substring(i + 1, i + 3)
                    try {
                        val b = hex.toInt(16).toByte()
                        buffer.write(byteArrayOf(b))
                        i += 3
                    } catch (_: Exception) {
                        // 如果不是合法的十六进制，退化处理
                        buffer.writeChar(c)
                        i++
                    }
                }

                '+' -> {
                    // 兼容表单提交：将 '+' 还原为空格
                    buffer.writeChar(' ')
                    i++
                }

                else -> {
                    buffer.writeChar(c)
                    i++
                }
            }
        }
        return buffer.readByteArray().decodeToString()
    }

}
