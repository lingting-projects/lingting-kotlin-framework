@file:OptIn(ExperimentalForeignApi::class)

package live.lingting.framework.crypto.digest

import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import live.lingting.framework.crypto.basic.MixinBasic
import platform.CoreCrypto.CC_MD5_CTX
import platform.CoreCrypto.CC_MD5_DIGEST_LENGTH
import platform.CoreCrypto.CC_MD5_Final
import platform.CoreCrypto.CC_MD5_Init
import platform.CoreCrypto.CC_MD5_Update
import platform.CoreCrypto.CC_SHA1_CTX
import platform.CoreCrypto.CC_SHA1_DIGEST_LENGTH
import platform.CoreCrypto.CC_SHA1_Final
import platform.CoreCrypto.CC_SHA1_Init
import platform.CoreCrypto.CC_SHA1_Update
import platform.CoreCrypto.CC_SHA256_CTX
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH
import platform.CoreCrypto.CC_SHA256_Final
import platform.CoreCrypto.CC_SHA256_Init
import platform.CoreCrypto.CC_SHA256_Update
import platform.CoreCrypto.CC_SHA512_CTX
import platform.CoreCrypto.CC_SHA512_DIGEST_LENGTH
import platform.CoreCrypto.CC_SHA512_Final
import platform.CoreCrypto.CC_SHA512_Init
import platform.CoreCrypto.CC_SHA512_Update

internal actual class PlatformDigest : MixinBasic {
    private val type: Digest.Type
    private val arena = Arena()
    private val contextPtr: CPointer<out CPointed>

    actual constructor(type: Digest.Type) {
        this.type = type
        this.contextPtr = when (type) {
            Digest.Type.MD5 -> {
                val ctx = arena.alloc<CC_MD5_CTX>()
                CC_MD5_Init(ctx.ptr)
                ctx.ptr
            }

            Digest.Type.SHA1 -> {
                val ctx = arena.alloc<CC_SHA1_CTX>()
                CC_SHA1_Init(ctx.ptr)
                ctx.ptr
            }

            Digest.Type.SHA256 -> {
                val ctx = arena.alloc<CC_SHA256_CTX>()
                CC_SHA256_Init(ctx.ptr)
                ctx.ptr
            }

            Digest.Type.SHA512 -> {
                val ctx = arena.alloc<CC_SHA512_CTX>()
                CC_SHA512_Init(ctx.ptr)
                ctx.ptr
            }
        }
    }

    actual override fun update(v: ByteArray, offset: Int, len: Int) {
        if (len <= 0) return

        v.usePinned { pinned ->
            val data = pinned.addressOf(offset)
            val uLen = len.toUInt()

            when (type) {
                Digest.Type.MD5 -> CC_MD5_Update(contextPtr.reinterpret(), data, uLen)
                Digest.Type.SHA1 -> CC_SHA1_Update(contextPtr.reinterpret(), data, uLen)
                Digest.Type.SHA256 -> CC_SHA256_Update(contextPtr.reinterpret(), data, uLen)
                Digest.Type.SHA512 -> CC_SHA512_Update(contextPtr.reinterpret(), data, uLen)
            }
        }
    }

    actual override fun calculate(): ByteArray {
        val digestSize = when (type) {
            Digest.Type.MD5 -> CC_MD5_DIGEST_LENGTH
            Digest.Type.SHA1 -> CC_SHA1_DIGEST_LENGTH
            Digest.Type.SHA256 -> CC_SHA256_DIGEST_LENGTH
            Digest.Type.SHA512 -> CC_SHA512_DIGEST_LENGTH
        }

        val result = ByteArray(digestSize)
        result.usePinned { pinned ->
            val out = pinned.addressOf(0).reinterpret<UByteVar>()

            when (type) {
                Digest.Type.MD5 -> CC_MD5_Final(out, contextPtr.reinterpret())
                Digest.Type.SHA1 -> CC_SHA1_Final(out, contextPtr.reinterpret())
                Digest.Type.SHA256 -> CC_SHA256_Final(out, contextPtr.reinterpret())
                Digest.Type.SHA512 -> CC_SHA512_Final(out, contextPtr.reinterpret())
            }
        }

        reset()
        return result
    }

    fun reset() {
        when (type) {
            Digest.Type.MD5 -> CC_MD5_Init(contextPtr.reinterpret())
            Digest.Type.SHA1 -> CC_SHA1_Init(contextPtr.reinterpret())
            Digest.Type.SHA256 -> CC_SHA256_Init(contextPtr.reinterpret())
            Digest.Type.SHA512 -> CC_SHA512_Init(contextPtr.reinterpret())
        }
    }

    actual override fun close() {
        arena.clear()
    }
}
