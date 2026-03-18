package live.lingting.framework.crypto.util

import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.readByteArray
import live.lingting.framework.crypto.digest.Digest
import live.lingting.framework.crypto.digest.Md5
import live.lingting.framework.crypto.digest.Sha1
import live.lingting.framework.crypto.digest.Sha256
import live.lingting.framework.crypto.digest.Sha512
import live.lingting.framework.data.DataSize
import live.lingting.framework.util.DataSizeUtils.mb
import live.lingting.framework.util.StringUtils.toHexString
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/4 17:24
 */
object DigestUtils {

    @JvmStatic
    @JvmOverloads
    fun RawSource.digest(digest: Digest, size: DataSize = 1.mb): ByteArray {
        val sink = Buffer()

        try {
            val l = this.readAtMostTo(sink, size.bytes)

            if (l > 0) {
                digest.update(sink.readByteArray())
            }
        } finally {
            sink.clear()
            digest.close()
        }

        return digest.calculate()
    }

    @JvmStatic
    fun String.toMd5Bytes(): ByteArray = encodeToByteArray().toMd5Bytes()

    @JvmStatic
    @JvmOverloads
    fun RawSource.toMd5Bytes(size: DataSize = 1.mb): ByteArray {
        val digest = Md5()
        return digest(digest, size)
    }

    @JvmStatic
    fun ByteArray.toMd5Bytes(): ByteArray {
        val degest = Md5()
        return degest.use { it.calculate(this) }
    }

    @JvmStatic
    fun String.toMd5Hex(): String = toMd5Bytes().toHexString()

    @JvmStatic
    fun RawSource.toMd5Hex(): String = toMd5Bytes().toHexString()

    @JvmStatic
    fun ByteArray.toMd5Hex(): String = toMd5Bytes().toHexString()

    @JvmStatic
    fun String.toSha1Bytes(): ByteArray = encodeToByteArray().toSha1Bytes()

    @JvmStatic
    @JvmOverloads
    fun RawSource.toSha1Bytes(size: DataSize = 1.mb): ByteArray {
        val digest = Sha1()
        return digest(digest, size)
    }

    @JvmStatic
    fun ByteArray.toSha1Bytes(): ByteArray {
        val degest = Sha1()
        return degest.use { it.calculate(this) }
    }

    @JvmStatic
    fun String.toSha1Hex(): String = toSha1Bytes().toHexString()

    @JvmStatic
    fun RawSource.toSha1Hex(): String = toSha1Bytes().toHexString()

    @JvmStatic
    fun ByteArray.toSha1Hex(): String = toSha1Bytes().toHexString()

    @JvmStatic
    fun String.toSha256Bytes(): ByteArray = encodeToByteArray().toSha256Bytes()

    @JvmStatic
    @JvmOverloads
    fun RawSource.toSha256Bytes(size: DataSize = 1.mb): ByteArray {
        val digest = Sha256()
        return digest(digest, size)
    }

    @JvmStatic
    fun ByteArray.toSha256Bytes(): ByteArray {
        val degest = Sha256()
        return degest.use { it.calculate(this) }
    }

    @JvmStatic
    fun String.toSha256Hex(): String = toSha256Bytes().toHexString()

    @JvmStatic
    fun RawSource.toSha256Hex(): String = toSha256Bytes().toHexString()

    @JvmStatic
    fun ByteArray.toSha256Hex(): String = toSha256Bytes().toHexString()

    @JvmStatic
    fun String.toSha512Bytes(): ByteArray = encodeToByteArray().toSha512Bytes()

    @JvmStatic
    @JvmOverloads
    fun RawSource.toSha512Bytes(size: DataSize = 1.mb): ByteArray {
        val digest = Sha512()
        return digest(digest, size)
    }

    @JvmStatic
    fun ByteArray.toSha512Bytes(): ByteArray {
        val degest = Sha512()
        return degest.use { it.calculate(this) }
    }

    @JvmStatic
    fun String.toSha512Hex(): String = toSha512Bytes().toHexString()

    @JvmStatic
    fun RawSource.toSha512Hex(): String = toSha512Bytes().toHexString()

    @JvmStatic
    fun ByteArray.toSha512Hex(): String = toSha512Bytes().toHexString()

}
