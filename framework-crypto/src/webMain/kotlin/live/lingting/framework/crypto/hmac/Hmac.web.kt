@file:OptIn(ExperimentalWasmJsInterop::class)

package live.lingting.framework.crypto.hmac

import korlibs.crypto.HMAC
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import live.lingting.framework.crypto.mac.Mac

internal actual class PlatformHmac : Mac.Platform {
    actual override val key: ByteArray
    private val type: Hmac.Type
    private var buffer = Buffer()

    actual constructor(key: ByteArray, type: Hmac.Type) {
        this.key = key
        this.type = type
    }

    actual override fun update(v: ByteArray, offset: Int, len: Int) {
        buffer.write(v, offset, len)
    }

    actual override fun calculate(): ByteArray {
        val bytes = buffer.readByteArray()
        val hash = when (type) {
            Hmac.Type.SHA1 -> HMAC.hmacSHA1(key, bytes)
            Hmac.Type.SHA256 -> HMAC.hmacSHA256(key, bytes)
            Hmac.Type.SHA512 -> HMAC.hmacSHA512(key, bytes)
        }
        buffer.clear()
        return hash.bytes
    }

}
