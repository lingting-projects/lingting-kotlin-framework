package live.lingting.kotlin.framework.util

import kotlinx.coroutines.test.runTest
import live.lingting.kotlin.framework.util.Base64Utils.base64
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Base64UtilsTest {

    @Test
    fun `test base64 encoding and decoding symmetry`() = runTest {
        // 原始数据
        val originalText = "Hello, 灵亭!"
        val originalBytes = originalText.encodeToByteArray()

        // 1. 测试 ByteArray -> String (Encode)
        val encodedString = originalBytes.base64()
        // "Hello, 灵亭!" 的 Base64 预期值
        // 注意：此处结果取决于 UTF-8 编码后的字节

        // 2. 测试 String -> ByteArray (Decode)
        val decodedBytes = encodedString.base64()
        val decodedText = decodedBytes.decodeToString()

        // 验证对称性：绕了一圈回来应该是一样的
        assertEquals(originalText, decodedText, "Base64 编解码后文本不一致")
        assertContentEquals(originalBytes, decodedBytes, "Base64 编解码后字节不一致")
    }

    @Test
    fun `test base64 with empty data`() = runTest {
        val emptyBytes = byteArrayOf()
        val encoded = emptyBytes.base64()

        assertEquals("", encoded, "空字节数组编码应为空字符串")
        assertContentEquals(emptyBytes, "".base64(), "空字符串解码应为空字节数组")
    }

    @Test
    fun `test known base64 values`() = runTest {
        // 验证标准值： "Kotlin" -> "S290bGlu"
        val input = "Kotlin".encodeToByteArray()
        val expectedBase64 = "S290bGlu"

        assertEquals(expectedBase64, input.base64())
        assertContentEquals(input, expectedBase64.base64())
    }

    @Test
    fun `test base64 with special characters`() = runTest {
        // 包含换行、符号的数据
        val complexData = byteArrayOf(0, 1, 2, 127, -1, -128)
        val encoded = complexData.base64()

        val decoded = encoded.base64()
        assertContentEquals(complexData, decoded, "复杂二进制数据编解码失败")
    }
}
