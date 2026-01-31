package live.lingting.kotlin.framework.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DataSizeTest {

    @Test
    fun `test of method with string parsing`() {
        // 1. 自动识别单位解析
        assertEquals(DataSize.ofBytes(1024), DataSize.of("1KB"))
        assertEquals(DataSize.ofMb(5), DataSize.of("5 mb"))
        assertEquals(DataSize.ofGb(1), DataSize.of("1.0GB"))

        // 2. 无单位默认解析为 Bytes
        assertEquals(DataSize.ofBytes(500), DataSize.of("500"))

        // 3. 异常输入
        assertNull(DataSize.of("abc"))
        assertNull(DataSize.of(""))
        assertNull(DataSize.of(null))
    }

    @Test
    fun `test unit conversion properties`() {
        val size = DataSize.ofMb(1) // 1024 * 1024 Bytes

        assertEquals(1024L * 1024, size.bytes)
        assertEquals(1024L, size.kb)
        assertEquals(1L, size.mb)
        assertEquals(0L, size.gb) // 整数除法
    }

    @Test
    fun `test auto unit detection`() {
        // 验证 unit 属性是否根据字节大小自动选择了合适的单位
        assertEquals(DataSizeUnit.BYTES, DataSize.ofBytes(500).unit)
        assertEquals(DataSizeUnit.KB, DataSize.ofBytes(1500).unit)
        assertEquals(DataSizeUnit.MB, DataSize.ofMb(10).unit)
    }

    @Test
    fun `test toString with scaleValue`() {
        // 验证集成 NumberUtils.toPlainString 和 DoubleUtils.scaleHalfUp
        val size = DataSize.ofBytes(1536) // 1.5 KB
        assertEquals("1.5 KB", size.toString())

        val size2 = DataSize.ofBytes(1024)
        assertEquals("1 KB", size2.toString()) // 移除末尾的 .0
    }

    @Test
    fun `test operator overloading`() {
        val oneMb = DataSize.ofMb(1)
        val twoMb = DataSize.ofMb(2)

        // 加法
        assertEquals(DataSize.ofMb(3), oneMb + twoMb)
        assertEquals(DataSize.ofMb(2), oneMb + (1024 * 1024))

        // 减法
        assertEquals(oneMb, twoMb - oneMb)

        // 乘法
        assertEquals(twoMb, oneMb * 2)

        // 除法
        assertEquals(0.5, oneMb / twoMb)
        assertEquals(512.0, oneMb / 2048)
    }

    @Test
    fun `test comparison and equality`() {
        val small = DataSize.ofKb(1)
        val large = DataSize.ofMb(1)
        val alsoSmall = DataSize.ofBytes(1024)

        assertTrue(small < large)
        assertEquals(small, alsoSmall)
        assertNotEquals(small, large)

        val set = setOf(small, alsoSmall)
        assertEquals(1, set.size, "HashCode 应基于 bytes 确保在 Set 中唯一")
    }
}
