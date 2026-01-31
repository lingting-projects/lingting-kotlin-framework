package live.lingting.kotlin.framework.util

import kotlin.jvm.JvmStatic
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow

/**
 * @author lingting 2025/12/24 14:56
 */
object DoubleUtils {

    /**
     * 四舍五入, 保留指定位数小数
     */
    @JvmStatic
    fun Double.scaleHalfUp(scale: Int): Double {
        if (this.isNaN() || this.isInfinite()) return this
        val factor = 10.0.pow(scale)
        // 将数值放大
        val shifted = this * factor
        // 精度补偿：加上一个微小的数值防止由于精度丢失导致的“差一点进位”
        return if (this >= 0) {
            floor(shifted + 0.500000000001) / factor
        } else {
            ceil(shifted - 0.500000000001) / factor
        }
    }

}
