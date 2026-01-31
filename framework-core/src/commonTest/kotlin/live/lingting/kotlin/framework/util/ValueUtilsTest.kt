package live.lingting.kotlin.framework.util

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValueUtilsTest {

    @Test
    fun `test isPresent with various types`() {
        // 1. Null 检查
        assertFalse(ValueUtils.isPresent(null), "Null 应该返回 false")

        // 2. CharSequence 检查 (假设 StringUtils.hasText 逻辑正常)
        assertFalse(ValueUtils.isPresent(""), "空字符串应该返回 false")
        assertFalse(ValueUtils.isPresent("  "), "仅包含空格的字符串应该返回 false")
        assertTrue(ValueUtils.isPresent("text"), "非空字符串应该返回 true")

        // 3. Collection 检查
        assertFalse(ValueUtils.isPresent(emptyList<String>()), "空列表应该返回 false")
        assertTrue(ValueUtils.isPresent(listOf("a")), "非空列表应该返回 true")

        // 4. Map 检查
        assertFalse(ValueUtils.isPresent(emptyMap<String, String>()), "空Map应该返回 false")
        assertTrue(ValueUtils.isPresent(mapOf("key" to "value")), "非空Map应该返回 true")

        // 5. Array 检查 (依赖 ArrayUtils.isArray/isEmpty)
        val emptyArray = arrayOf<String>()
        val nonEmptyArray = arrayOf("val")
        assertFalse(ValueUtils.isPresent(emptyArray), "空数组应该返回 false")
        assertTrue(ValueUtils.isPresent(nonEmptyArray), "非空数组应该返回 true")

        // 6. 普通对象检查
        assertTrue(ValueUtils.isPresent(123), "非空普通对象应该返回 true")
        assertTrue(ValueUtils.isPresent(Any()), "任意 Object 应该返回 true")
    }

    @Test
    fun `test uuid generation`() = runTest {
        // 使用协程测试环境（虽然目前方法内没有挂起函数，但符合你的要求）
        val uuid = ValueUtils.uuid()

        // 验证标准 UUID 格式: 8-4-4-4-12 (包含短横线)
        val regex = Regex("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
        assertTrue(regex.matches(uuid), "生成的 UUID 格式不正确: $uuid")
    }

    @Test
    fun `test simpleUuid generation`() = runTest {
        val simpleUuid = ValueUtils.simpleUuid()

        // 验证 Simple UUID 格式: 32位 16 进制字符 (无短横线)
        val regex = Regex("^[0-9a-f]{32}$")
        assertTrue(regex.matches(simpleUuid), "生成的 Simple UUID 格式不正确: $simpleUuid")
        assertEquals(32, simpleUuid.length)
    }
}
