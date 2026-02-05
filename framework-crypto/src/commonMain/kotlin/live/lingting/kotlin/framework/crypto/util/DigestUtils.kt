package live.lingting.kotlin.framework.crypto.util

import kotlinx.io.RawSource
import kotlinx.io.buffered
import kotlinx.io.readByteArray
import live.lingting.kotlin.framework.util.StringUtils.toHexString
import org.kotlincrypto.hash.md.MD5
import org.kotlincrypto.hash.sha1.SHA1
import org.kotlincrypto.hash.sha2.SHA256
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/4 17:24
 */
object DigestUtils {

    @JvmStatic
    fun String.toMd5Bytes(): ByteArray = encodeToByteArray().toMd5Bytes()

    @JvmStatic
    fun RawSource.toMd5Bytes(): ByteArray = buffered().use { it.readByteArray().toMd5Bytes() }

    @JvmStatic
    fun ByteArray.toMd5Bytes(): ByteArray {
        val v = MD5()
        return v.digest(this)
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
    fun RawSource.toSha1Bytes(): ByteArray = buffered().use { it.readByteArray().toSha1Bytes() }

    @JvmStatic
    fun ByteArray.toSha1Bytes(): ByteArray {
        val v = SHA1()
        return v.digest(this)
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
    fun RawSource.toSha256Bytes(): ByteArray = buffered().use { it.readByteArray().toSha256Bytes() }

    @JvmStatic
    fun ByteArray.toSha256Bytes(): ByteArray {
        val v = SHA256()
        return v.digest(this)
    }

    @JvmStatic
    fun String.toSha256Hex(): String = toSha256Bytes().toHexString()

    @JvmStatic
    fun RawSource.toSha256Hex(): String = toSha256Bytes().toHexString()

    @JvmStatic
    fun ByteArray.toSha256Hex(): String = toSha256Bytes().toHexString()

}
