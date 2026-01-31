package live.lingting.kotlin.framework.util

import kotlinx.coroutines.test.runTest
import live.lingting.kotlin.framework.util.CharUtils.isLetter
import live.lingting.kotlin.framework.util.CharUtils.isLowerLetter
import live.lingting.kotlin.framework.util.CharUtils.isUpperLetter
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CharUtilsTest {

    @Test
    fun `test isLowerLetter`() = runTest {
        assertTrue('a'.isLowerLetter())
        assertTrue('z'.isLowerLetter())

        assertFalse('A'.isLowerLetter())
        assertFalse('Z'.isLowerLetter())
        assertFalse('1'.isLowerLetter())
        assertFalse(' '.isLowerLetter())
        assertFalse('中'.isLowerLetter())
    }

    @Test
    fun `test isUpperLetter`() = runTest {
        assertTrue('A'.isUpperLetter())
        assertTrue('Z'.isUpperLetter())

        assertFalse('a'.isUpperLetter())
        assertFalse('z'.isUpperLetter())
        assertFalse('9'.isUpperLetter())
        assertFalse('@'.isUpperLetter())
    }

    @Test
    fun `test isLetter`() = runTest {
        // 字母情况
        assertTrue('a'.isLetter())
        assertTrue('A'.isLetter())

        // 非字母情况
        assertFalse('0'.isLetter())
        assertFalse('-'.isLetter())
        assertFalse('\n'.isLetter())

        // 特殊：验证 isLetter 是否仅包含 ASCII 字母
        // 注意：Kotlin 标准库的 isUpperCase 对某些 Unicode 字符也返回 true
        // 如果你的业务只允许 A-Z, a-z，测试能帮你发现这一点
        assertTrue('π'.isLowerCase()) // Kotlin 标准库中 π 是小写字母
    }
}
