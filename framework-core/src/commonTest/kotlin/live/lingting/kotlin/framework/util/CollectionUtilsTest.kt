package live.lingting.kotlin.framework.util

import kotlinx.coroutines.test.runTest
import live.lingting.kotlin.framework.util.CollectionUtils.flatten
import live.lingting.kotlin.framework.util.CollectionUtils.split
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CollectionUtilsTest {

    @Test
    fun `test isMulti`() {
        assertTrue(CollectionUtils.isMulti(listOf(1)))
        assertTrue(CollectionUtils.isMulti(arrayOf(1)))
        assertTrue(CollectionUtils.isMulti(listOf(1).iterator()))
        assertFalse(CollectionUtils.isMulti("String is not multi"))
        assertFalse(CollectionUtils.isMulti(null))
    }

    @Test
    fun `test multiToList`() {
        // 1. 测试单体对象转 List
        val single = "test"
        assertEquals(listOf(single), CollectionUtils.multiToList(single))

        // 2. 测试 Collection 转 List
        val set = setOf(1, 2, 2)
        assertEquals(listOf(1, 2), CollectionUtils.multiToList(set))

        // 3. 测试 Iterator 转 List
        val iterator = listOf("a", "b").iterator()
        assertEquals(listOf("a", "b"), CollectionUtils.multiToList(iterator))

        // 4. 测试 Null
        assertEquals(emptyList(), CollectionUtils.multiToList(null))
    }

    @Test
    fun `test extract`() {
        val data = listOf(1, 2, 3, 4, 5)

        // 提取 3 个
        val extracted = CollectionUtils.extract(data, 3)
        assertEquals(3, extracted.size)
        assertEquals(listOf(1, 2, 3), extracted)

        // 提取超过总数的数量
        val extractedAll = CollectionUtils.extract(data, 10)
        assertEquals(5, extractedAll.size)
    }

    @Test
    fun `test split`() = runTest {
        val list = listOf(1, 2, 3, 4, 5)

        // 每组 2 个
        val chunks = list.split(2)
        assertEquals(3, chunks.size)
        assertEquals(listOf(1, 2), chunks[0])
        assertEquals(listOf(3, 4), chunks[1])
        assertEquals(listOf(5), chunks[2])

        // 恰好整除的情况
        val exactChunks = listOf(1, 2, 3, 4).split(2)
        assertEquals(2, exactChunks.size)
    }

    @Test
    fun `test toMap`() {
        val keys = listOf("a", "b", "c")
        val values = listOf(1, 2) // 少一个值

        val map = CollectionUtils.toMap(keys, values)
        assertEquals(2, map.size)
        assertEquals(1, map["a"])
        assertEquals(2, map["b"])
        assertFalse(map.containsKey("c"))
    }

    @Test
    fun `test flattenMap`() = runTest {
        val nestedMap = mapOf(
            "user" to mapOf(
                "name" to "lingting",
                "tags" to listOf("kotlin", "developer")
            ),
            "version" to 1
        )

        val flattened = nestedMap.flatten()

        // 验证扁平化结果
        assertEquals("lingting", flattened["user.name"])
        assertEquals("kotlin", flattened["user.tags[0]"])
        assertEquals("developer", flattened["user.tags[1]"])
        assertEquals(1, flattened["version"])
        assertEquals(4, flattened.size)
    }
}
