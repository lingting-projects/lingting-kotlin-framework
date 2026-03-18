package live.lingting.framework.crypto.hmac

import kotlinx.cinterop.Arena
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import live.lingting.framework.crypto.mac.Mac
import platform.CoreCrypto.CCHmacAlgorithm
import platform.CoreCrypto.CCHmacContext
import platform.CoreCrypto.CCHmacFinal
import platform.CoreCrypto.CCHmacInit
import platform.CoreCrypto.CCHmacUpdate
import platform.CoreCrypto.CC_SHA1_DIGEST_LENGTH
import platform.CoreCrypto.CC_SHA256_DIGEST_LENGTH
import platform.CoreCrypto.CC_SHA512_DIGEST_LENGTH
import platform.CoreCrypto.kCCHmacAlgSHA1
import platform.CoreCrypto.kCCHmacAlgSHA256
import platform.CoreCrypto.kCCHmacAlgSHA512

@OptIn(ExperimentalForeignApi::class)
internal actual class PlatformHmac : Mac.Platform {
    actual override val key: ByteArray

    // 使用 Arena 管理当前实例的所有 Native 内存
    private val arena = Arena()

    // 在 Arena 中分配 HMAC 上下文
    private val context: CPointer<CCHmacContext> = arena.alloc<CCHmacContext>().ptr

    private val algorithm: CCHmacAlgorithm
    private val digestLength: Int

    actual constructor(key: ByteArray, type: Hmac.Type) {
        this.key = key

        val (algo, len) = when (type) {
            Hmac.Type.SHA1 -> kCCHmacAlgSHA1 to CC_SHA1_DIGEST_LENGTH
            Hmac.Type.SHA256 -> kCCHmacAlgSHA256 to CC_SHA256_DIGEST_LENGTH
            Hmac.Type.SHA512 -> kCCHmacAlgSHA512 to CC_SHA512_DIGEST_LENGTH
        }
        this.algorithm = algo
        this.digestLength = len

        initContext()
    }

    private fun initContext() {
        key.usePinned { pinnedKey ->
            CCHmacInit(
                context,
                algorithm,
                pinnedKey.addressOf(0),
                key.size.toULong()
            )
        }
    }

    actual override fun update(v: ByteArray, offset: Int, len: Int) {
        if (len <= 0) return
        v.usePinned { pinned ->
            CCHmacUpdate(
                context,
                pinned.addressOf(offset),
                len.toULong()
            )
        }
    }

    actual override fun calculate(): ByteArray {
        val result = ByteArray(digestLength)
        result.usePinned { pinnedResult ->
            CCHmacFinal(context, pinnedResult.addressOf(0))
        }

        // 计算完成后重置状态，以便复用（与 JVM 行为一致）
        initContext()
        return result
    }

    /**
     * 关键：显式释放内存
     */
    actual override fun close() {
        // 清除 Arena 区域，这会释放在这个 Arena 中分配的所有内存（包括 context）
        arena.clear()
    }
}
