package live.lingting.kotlin.framework.util

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberUtilsTest {

    @Test
    fun `test toPlainString with various number types`() = runTest {
        // 1. 整数测试 (Int, Long)
        assertEquals("100", NumberUtils.toPlainString(100))
        assertEquals("0", NumberUtils.toPlainString(0L))
        assertEquals("-50", NumberUtils.toPlainString(-50))

        // 2. 带小数的 Double/Float 测试
        assertEquals("1.2", NumberUtils.toPlainString(1.200))
        assertEquals("3.14", NumberUtils.toPlainString(3.14000))
        assertEquals("0.5", NumberUtils.toPlainString(0.5f))

        // 3. 移除多余小数点测试 (例如 1.0 -> 1)
        assertEquals("1", NumberUtils.toPlainString(1.0))
        assertEquals("10", NumberUtils.toPlainString(10.000))

        // 5. 负数小数测试
        assertEquals("-1.23", NumberUtils.toPlainString(-1.230))
        assertEquals("-1", NumberUtils.toPlainString(-1.00))
    }

    @Test
    fun `test toPlainString edge cases`() = runTest {
        // 极小值
        assertEquals("0.1", NumberUtils.toPlainString(0.10000000000000000000))
        assertEquals("0.000000000000000000001", NumberUtils.toPlainString(0.000000000000000000001))
        // 零
        assertEquals("0", NumberUtils.toPlainString(0.000))
    }
}
