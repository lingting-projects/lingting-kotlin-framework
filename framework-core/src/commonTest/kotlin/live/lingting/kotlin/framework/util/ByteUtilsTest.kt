package live.lingting.kotlin.framework.util

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ByteUtilsTest {

    private val cr = '\r'.code.toByte()
    private val lf = '\n'.code.toByte()

    @Test
    fun `test toArray`() {
        val list = listOf<Byte>(1, 2, 3)
        val expected = byteArrayOf(1, 2, 3)
        assertContentEquals(expected, ByteUtils.toArray(list))
    }

    @Test
    fun `test isEndLine`() {
        // 测试双字节 CRLF
        assertTrue(ByteUtils.isEndLine(cr, lf))
        assertFalse(ByteUtils.isEndLine(lf, cr))
        assertFalse(ByteUtils.isEndLine('a'.code.toByte(), lf))

        // 测试单字节 LF
        assertTrue(ByteUtils.isEndLine(lf))
        assertFalse(ByteUtils.isEndLine(cr))
    }

    @Test
    fun `test isLine for both List and ByteArray`() {
        // 1. CRLF 结尾
        val dataCRLF = listOf('a'.code.toByte(), cr, lf)
        assertTrue(ByteUtils.isLine(dataCRLF))
        assertTrue(ByteUtils.isLine(ByteUtils.toArray(dataCRLF)))

        // 2. LF 结尾
        val dataLF = listOf('a'.code.toByte(), lf)
        assertTrue(ByteUtils.isLine(dataLF))
        assertTrue(ByteUtils.isLine(ByteUtils.toArray(dataLF)))

        // 3. 非行结尾
        val dataNone = listOf('a'.code.toByte(), 'b'.code.toByte())
        assertFalse(ByteUtils.isLine(dataNone))

        // 4. 空数据
        assertFalse(ByteUtils.isLine(emptyList<Byte>()))
    }

    @Test
    fun `test trimEndLine`() = runTest {
        // 场景 A: 移除 CRLF (\r\n)
        val withCRLF = listOf('H'.code.toByte(), 'i'.code.toByte(), cr, lf)
        assertContentEquals("Hi".encodeToByteArray(), ByteUtils.trimEndLine(withCRLF))

        // 场景 B: 移除 LF (\n)
        val withLF = listOf('H'.code.toByte(), 'i'.code.toByte(), lf)
        assertContentEquals("Hi".encodeToByteArray(), ByteUtils.trimEndLine(withLF))

        // 场景 C: 没有行尾符
        val noEnd = listOf('H'.code.toByte(), 'i'.code.toByte())
        assertContentEquals("Hi".encodeToByteArray(), ByteUtils.trimEndLine(noEnd))

        // 场景 D: 仅有行尾符
        val onlyCRLF = listOf(cr, lf)
        assertContentEquals(byteArrayOf(), ByteUtils.trimEndLine(onlyCRLF))

        // 场景 E: 空集合
        assertContentEquals(byteArrayOf(), ByteUtils.trimEndLine(emptyList()))
    }
}
