package live.lingting.kotlin.framework.util

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import live.lingting.kotlin.framework.util.BufferUtils.write
import live.lingting.kotlin.framework.util.BufferUtils.writeChar
import live.lingting.kotlin.framework.util.CharUtils.isLowerLetter
import live.lingting.kotlin.framework.util.CharUtils.isUpperLetter
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmStatic
import kotlin.math.max

/**
 * @author lingting
 */
object StringUtils {

    const val BOM_UTF8: String = "\uFEFF"

    const val BOM_UTF16B: String = "\uFEFF"

    const val BOM_UTF16S: String = "\uFFFE"

    const val BOM_UTF32B: String = "\u0000FEFF"

    const val BOM_UTF32S: String = "\uFFFFE0000"

    /**
     * 指定字符串是否存在可见字符
     * @param str 字符串
     * @return boolean
     */
    @JvmStatic
    @OptIn(ExperimentalContracts::class)
    fun hasText(str: CharSequence?): Boolean {
        contract {
            returns(true) implies (str != null)
        }

        if (str.isNullOrBlank()) {
            return false
        }

        for (i in 0 until str.length) {
            // 如果是非空白字符
            if (!str[i].isWhitespace()) {
                return true
            }
        }

        return false
    }

    @JvmStatic
    fun join(iterable: Iterable<*>?, delimiter: String): String {
        if (iterable == null) {
            return ""
        }

        return join(iterable.iterator(), delimiter)
    }

    @JvmStatic
    fun join(iterator: Iterator<*>?, delimiter: String): String {
        if (iterator == null) {
            return ""
        }

        val builder = StringBuilder()
        while (iterator.hasNext()) {
            val next = iterator.next() ?: continue
            builder.append(next)

            if (iterator.hasNext()) {
                builder.append(delimiter)
            }
        }

        return builder.toString()
    }

    @JvmStatic
    fun String?.firstLower(): String {
        if (this == null) {
            return ""
        }

        if (!hasText(this)) {
            return this
        }

        val c = this[0]
        if (c.isUpperLetter()) {
            return c.lowercaseChar().toString() + substring(1)
        }
        return this
    }

    @JvmStatic
    fun String?.firstUpper(): String {
        if (this == null) {
            return ""
        }
        if (!hasText(this)) {
            return this
        }

        val c = this[0]
        if (c.isLowerLetter()) {
            return c.uppercaseChar().toString() + substring(1)
        }
        return this
    }

    /**
     * 驼峰字符串转下划线字符串
     * eg: humpToUnderscore -> hump_to_underscore
     */
    @JvmStatic
    fun humpToUnderscore(str: String): String {
        val buffer = Buffer()
        for (i in 0 until str.length) {
            val c = str[i]
            // 大写字母处理
            if (c.isUpperLetter()) {
                // 如果不是第一个大写字母, 插入下划线 _
                if (i > 0) {
                    buffer.writeChar('_')
                }
                // 转小写
                buffer.writeChar(c.lowercaseChar())
            } else {
                buffer.writeChar(c)
            }
        }

        return buffer.readByteArray().decodeToString()
    }

    /**
     * 下划线字符串转驼峰字符串
     * eg-> hump_to_underscore
     */
    @JvmStatic
    fun underscoreToHump(str: String): String {
        val buffer = Buffer()
        var upper = false
        for (i in 0 until str.length) {
            val c = str[i]
            // 如果是下划线, 下一个要转大写
            if (c == '_') {
                upper = true
                continue
            }
            // 要转大写
            if (upper) {
                buffer.writeChar(c.uppercaseChar())
                upper = false
            } else {
                buffer.writeChar(c)
            }
        }

        return buffer.readByteArray().decodeToString()
    }

    @JvmStatic
    fun ByteArray.hex(): String {
        val buffer = Buffer()
        val hexChars = "0123456789abcdef"
        for (b in this) {
            val i = b.toInt() and 0xFF
            buffer.write(hexChars[i shr 4])
            buffer.write(hexChars[i and 0x0F])
        }
        return buffer.readByteArray().decodeToString()
    }

    @JvmStatic
    fun String.hex(): ByteArray {
        val length = length
        require(length % 2 == 0) { "Invalid hexadecimal string" }

        val byteArray = ByteArray(length / 2)
        var i = 0
        while (i < length) {
            // 16进制单字符
            val h: String = substring(i, i + 2)
            // 转byte
            val b = h.toInt(16).toByte()
            byteArray[i / 2] = b
            i += 2
        }

        return byteArray
    }

    /**
     * 往前缀追加 指定数量的指定字符
     * @param prefix 前缀
     * @param count  数量
     * @param str    指定字符
     * @return 追加完成后的字符串
     */
    @JvmStatic
    fun append(prefix: String, count: Int, str: String): String {
        return prefix + str.repeat(max(0, count))
    }

    @JvmStatic
    fun cleanBom(string: String): String {
        return string.replace(BOM_UTF32S, "")
            .replace(BOM_UTF32B, "")
            .replace(BOM_UTF16S, "")
            .replace(BOM_UTF16B, "")
            .replace(BOM_UTF8, "")
    }

    @JvmStatic
    fun substringBefore(str: String, separator: String): String {
        val pos: Int = str.indexOf(separator)
        return if (pos == -1) str else str.substring(0, pos)
    }

    @JvmStatic
    fun substringBeforeLast(str: String, separator: String): String {
        val pos: Int = str.lastIndexOf(separator)
        return if (pos == -1) str else str.substring(0, pos)
    }

    @JvmStatic
    fun substringAfter(str: String, separator: String): String {
        val pos: Int = str.indexOf(separator)
        return if (pos == -1) "" else str.substring(pos + separator.length)
    }

    @JvmStatic
    fun substringAfterLast(str: String, separator: String): String {
        val pos: Int = str.lastIndexOf(separator)
        return if (pos == -1) str else str.substring(pos + separator.length)
    }

    @JvmStatic
    fun deleteLast(builder: StringBuilder): StringBuilder {
        if (builder.isEmpty()) {
            return builder
        }
        val index = builder.length - 1
        return builder.deleteAt(index)
    }

}
