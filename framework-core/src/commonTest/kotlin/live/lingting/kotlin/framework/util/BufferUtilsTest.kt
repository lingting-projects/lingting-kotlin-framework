package live.lingting.kotlin.framework.util

import kotlinx.coroutines.test.runTest
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import live.lingting.kotlin.framework.util.BufferUtils.write
import live.lingting.kotlin.framework.util.BufferUtils.writeChar
import kotlin.test.Test
import kotlin.test.assertEquals

class BufferUtilsTest {

    @Test
    fun `test write char`() = runTest {
        val buffer = Buffer()

        // 测试 Buffer.write(Char)
        buffer.write('A')
        // 测试 Buffer.writeChar(Char)
        buffer.writeChar('B')

        val result = buffer.readByteArray().decodeToString()
        assertEquals("AB", result, "字符写入结果不符合预期")
    }

    @Test
    fun `test write string`() = runTest {
        val buffer = Buffer()
        val testString = "Hello Kotlin"

        // 测试 Buffer.write(String)
        buffer.write(testString)

        val result = buffer.readByteArray().decodeToString()
        assertEquals(testString, result, "字符串写入结果不符合预期")
    }

    @Test
    fun `test mixed write`() = runTest {
        val buffer = Buffer()

        buffer.write("ID:")
        buffer.write('1')
        buffer.writeChar('A')

        val result = buffer.readByteArray().decodeToString()
        assertEquals("ID:1A", result, "混合写入结果不符合预期")
    }
}
