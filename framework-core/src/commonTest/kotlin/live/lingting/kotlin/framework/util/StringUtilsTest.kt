package live.lingting.kotlin.framework.util

import kotlinx.coroutines.test.runTest
import live.lingting.kotlin.framework.util.StringUtils.firstLower
import live.lingting.kotlin.framework.util.StringUtils.firstUpper
import live.lingting.kotlin.framework.util.StringUtils.hex
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StringUtilsTest {

    @Test
    fun `test hasText`() {
        assertFalse(StringUtils.hasText(null))
        assertFalse(StringUtils.hasText(""))
        assertFalse(StringUtils.hasText("   "))
        assertTrue(StringUtils.hasText(" a "))
        assertTrue(StringUtils.hasText("abc"))
    }

    @Test
    fun `test join`() {
        val list = listOf("apple", "banana", null, "orange")
        // 注意：你的 join 实现中使用了 iterator.next() ?: continue，会跳过 null
        assertEquals("apple,banana,orange", StringUtils.join(list, ","))
        assertEquals("", StringUtils.join(null as Iterable<*>?, ","))
    }

    @Test
    fun `test casing transformations`() {
        // First Lower
        assertEquals("apple", "Apple".firstLower())
        assertEquals("a", "A".firstLower())
        assertEquals(" apple", " apple".firstLower())
        assertEquals("", (null as String?).firstLower())

        // First Upper
        assertEquals("Apple", "apple".firstUpper())
        assertEquals("A", "a".firstUpper())
    }

    @Test
    fun `test naming conversions`() = runTest {
        // Hump to Underscore
        assertEquals("hump_to_underscore", StringUtils.humpToUnderscore("humpToUnderscore"))
        assertEquals("user_id", StringUtils.humpToUnderscore("userId"))

        // Underscore to Hump
        assertEquals("humpToUnderscore", StringUtils.underscoreToHump("hump_to_underscore"))
        assertEquals("userId", StringUtils.underscoreToHump("user_id"))
    }

    @Test
    fun `test hex conversions`() {
        val original = "hello".encodeToByteArray()
        val hexString = original.hex()

        // "hello" -> 68656c6c6f
        assertEquals("68656c6c6f", hexString)
        assertContentEquals(original, hexString.hex())
    }

    @Test
    fun `test substring operations`() {
        val str = "live.lingting.kotlin"

        assertEquals("live.lingting", StringUtils.substringBeforeLast(str, "."))
        assertEquals("live", StringUtils.substringBefore(str, "."))
        assertEquals("lingting.kotlin", StringUtils.substringAfter(str, "."))
        assertEquals("kotlin", StringUtils.substringAfterLast(str, "."))

        // 分隔符不存在的情况
        assertEquals(str, StringUtils.substringBefore(str, "?"))
        assertEquals("", StringUtils.substringAfter(str, "?"))
    }

    @Test
    fun `test cleanBom`() {
        val withBom = StringUtils.BOM_UTF8 + "Hello"
        assertEquals("Hello", StringUtils.cleanBom(withBom))
        assertEquals("Standard", StringUtils.cleanBom("Standard"))
    }

    @Test
    fun `test deleteLast`() {
        val sb = StringBuilder("Kotlin")
        StringUtils.deleteLast(sb)
        assertEquals("Kotli", sb.toString())

        val emptySb = StringBuilder("")
        StringUtils.deleteLast(emptySb)
        assertEquals("", emptySb.toString())
    }
}
