package live.lingting.kotlin.framework.util

import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/1/30 17:14
 */
object NumberUtils {

    /**
     * 转字符串, 规避科学计数法
     */
    @JvmStatic
    fun toString(n: Number): String {
        val original = n.toString()
        if (!original.contains('E', ignoreCase = true)) {
            return original
        }

        // 拆分 底数 和 指数
        val parts = original.split('e', 'E')
        // 去掉小数点
        val base = parts[0].replace(".", "")
        val exponent = parts[1].toInt()

        val isNegative = original.startsWith("-")
        val absoluteBase = if (isNegative) base.substring(1) else base

        // 获取原小数点的位置
        val dotIndex = parts[0].indexOf('.')
        val initialPrecision = if (dotIndex == -1) 0 else parts[0].length - dotIndex - 1

        val sb = StringBuilder()
        if (isNegative) sb.append("-")

        if (exponent > 0) {
            // 指数为正，小数点向右移动
            sb.append(absoluteBase)
            val moveRight = exponent - initialPrecision
            if (moveRight > 0) {
                repeat(moveRight) { sb.append('0') }
            } else if (moveRight < 0) {
                // 在中间插入小数点
                sb.insert(sb.length + moveRight, '.')
            }
        } else {
            // 指数为负，小数点向左移动
            sb.append("0.")
            val moveLeft = (-exponent) - 1
            repeat(moveLeft) { sb.append('0') }
            sb.append(absoluteBase)
        }

        return sb.toString().trimEnd('.').let {
            if (it.isEmpty() || it == "-") "0" else it
        }
    }

    /**
     * 转字符串, 移除小数位末尾的0
     */
    @JvmStatic
    fun toPlainString(n: Number): String {
        val s = toString(n)
        // 如果不包含小数点，直接返回（例如 Long, Int）
        if (!s.contains(".")) {
            return s
        }

        return s
            // 移除末尾的 '0'
            .dropLastWhile { it == '0' }
            // 如果移除 0 后剩下小数点，也移除
            .removeSuffix(".")
    }

}
