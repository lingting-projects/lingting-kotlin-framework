package live.lingting.kotlin.framework.util

import kotlinx.coroutines.test.runTest
import live.lingting.kotlin.framework.util.DoubleUtils.scaleHalfUp
import kotlin.test.Test
import kotlin.test.assertEquals

class DoubleUtilsTest {

    @Test
    fun `test scaleHalfUp basic rounding`() = runTest {
        // 1. 常规保留两位小数
        assertEquals(3.14, 3.14159.scaleHalfUp(2))
        assertEquals(3.14, 3.144.scaleHalfUp(2))

        // 2. 临界点 0.5 进位
        assertEquals(3.15, 3.145.scaleHalfUp(2))
        assertEquals(3.15, 3.1451.scaleHalfUp(2))
    }

    @Test
    fun `test scaleHalfUp with different scales`() = runTest {
        val num = 123.456

        // 保留 0 位小数 (取整)
        assertEquals(123.0, num.scaleHalfUp(0))

        // 保留 1 位小数
        assertEquals(123.5, num.scaleHalfUp(1))

        // 保留 4 位小数 (补位后值不变)
        assertEquals(123.456, num.scaleHalfUp(4))
    }

    @Test
    fun `test scaleHalfUp with negative numbers`() = runTest {
        // 负数的四舍五入行为
        assertEquals(-2.0, (-1.5).scaleHalfUp(0))

        assertEquals(-2.0, (-1.51).scaleHalfUp(0))

        assertEquals(-3.14, (-3.141).scaleHalfUp(2))
        assertEquals(-3.15, (-3.145).scaleHalfUp(2))
    }

    @Test
    fun `test scaleHalfUp edge cases`() = runTest {
        // 0 的处理
        assertEquals(0.0, 0.0.scaleHalfUp(2))

        // 极大值 (可能会受 pow(scale) 溢出影响，但常规 scale 没问题)
        assertEquals(1000000.12, 1000000.1234.scaleHalfUp(2))
    }
}
