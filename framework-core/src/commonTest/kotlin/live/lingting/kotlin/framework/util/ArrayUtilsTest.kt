package live.lingting.kotlin.framework.util

import live.lingting.kotlin.framework.util.ArrayUtils.contains
import live.lingting.kotlin.framework.util.ArrayUtils.containsIgnoreCase
import live.lingting.kotlin.framework.util.ArrayUtils.indexOf
import live.lingting.kotlin.framework.util.ArrayUtils.isArray
import live.lingting.kotlin.framework.util.ArrayUtils.isEmpty
import live.lingting.kotlin.framework.util.ArrayUtils.sub
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ArrayUtilsTest {

    @Test
    fun `test isArray with various types`() {
        // 对象数组
        assertTrue(arrayOf("a", "b").isArray())
        // 基本类型数组
        assertTrue(intArrayOf(1, 2).isArray())
        assertTrue(byteArrayOf(0x01).isArray())
        assertTrue(booleanArrayOf(true).isArray())
        assertTrue(charArrayOf('c').isArray())

        // 非数组
        assertFalse("string".isArray())
        assertFalse(listOf(1, 2).isArray())
        assertFalse(null.isArray())
    }

    @Test
    fun `test arrayToList`() {
        val intArray = intArrayOf(1, 2, 3)
        val list = ArrayUtils.arrayToList(intArray)
        assertEquals(listOf(1, 2, 3), list)

        val stringArray = arrayOf("a", "b")
        assertEquals(listOf("a", "b"), ArrayUtils.arrayToList(stringArray))

        assertEquals(emptyList<Any>(), ArrayUtils.arrayToList(null))
    }

    @Test
    fun `test isEmpty`() {
        // 数组
        assertTrue(arrayOf<String>().isEmpty())
        assertFalse(arrayOf("a").isEmpty())
        assertTrue(intArrayOf().isEmpty())

        // 集合/Map 顺便支持的测试
        assertTrue(emptyList<String>().isEmpty())
        assertTrue(emptyMap<String, String>().isEmpty())

        // 非容器对象
        assertFalse(123.isEmpty())
    }

    @Test
    fun `test indexOf and contains`() {
        val array = arrayOf("apple", "banana", "cherry")

        assertEquals(1, array.indexOf("banana"))
        assertEquals(ArrayUtils.NOT_FOUNT, array.indexOf("pear"))

        assertTrue(array.contains("cherry"))
        assertFalse(array.contains("watermelon"))
    }

    @Test
    fun `test containsIgnoreCase`() {
        val array = arrayOf("Kotlin", "Java", "Swift")

        assertTrue(array.containsIgnoreCase("kotlin"))
        assertTrue(array.containsIgnoreCase("JAVA"))
        assertFalse(array.containsIgnoreCase("Python"))
    }

    @Test
    fun `test arrayEquals across types`() {
        val a = intArrayOf(1, 2, 3, 4, 5)
        val b = arrayOf(1, 2, 3, 4, 5) // 对象数组
        val c = intArrayOf(3, 4)

        // 1. 完全相等测试 (虽然一个是基本类型数组，一个是对象数组)
        assertTrue(ArrayUtils.arrayEquals(a, b))

        // 2. 部分匹配测试: a[2..3] 是否等于 c[0..1]
        // a[2]=3, a[3]=4; c[0]=3, c[1]=4
        assertTrue(ArrayUtils.arrayEquals(2, a, 0, c, 2))

        // 3. 长度不一致测试
        assertFalse(ArrayUtils.arrayEquals(0, a, 0, c, null))

        // 4. 越界测试
        assertFalse(ArrayUtils.arrayEquals(4, a, 0, c, 5))
    }

    @Test
    fun `test sub array`() {
        val array = arrayOf(0, 1, 2, 3, 4)

        // 正常截取 [1, 3) -> 1, 2
        val sub1 = array.sub(1, 3)
        assertContentEquals(arrayOf(1, 2), sub1)

        // 负数起始处理 (max(0, start))
        val sub2 = array.sub(-1, 2)
        assertContentEquals(arrayOf(0, 1), sub2)

        // 超过长度处理 (min(size, end))
        val sub3 = array.sub(3, 10)
        assertContentEquals(arrayOf(3, 4), sub3)

        // 空数组截取
        val empty = arrayOf<Int>().sub(0, 5)
        assertTrue(empty.isEmpty())
    }
}
