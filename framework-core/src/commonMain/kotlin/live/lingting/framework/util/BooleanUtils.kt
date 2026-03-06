package live.lingting.framework.util

import live.lingting.framework.util.ArrayUtils.containsIgnoreCase
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object BooleanUtils {

    /**
     * @author lingting 2023-05-06 14:16
     */
    @JvmField
    val STR_TRUE = arrayOf("1", "true", "yes", "ok", "y", "t")

    @JvmField
    val STR_FALSE = arrayOf("0", "false", "no", "n", "f")

    @JvmStatic
    fun <T> T.isTrue(): Boolean {
        if (this == null) {
            return false
        }
        if (this is String) {
            return STR_TRUE.containsIgnoreCase(this)
        }
        if (this is Number) {
            return toDouble() > 0
        }
        if (this is Boolean) {
            return this == true
        }
        return false
    }

    @JvmStatic
    fun <T> T.isFalse(): Boolean {
        if (this == null) {
            return false
        }
        if (this is String) {
            return STR_FALSE.containsIgnoreCase(this)
        }
        if (this is Number) {
            return toDouble() <= 0
        }
        if (this is Boolean) {
            return this == false
        }
        return false
    }

    /**
     * @author lingting 2024/11/15 15:49
     */
    @JvmStatic
    fun <T : Boolean?> T.ifTrue(runnable: () -> Unit) {
        if (this == true) runnable()
    }

    @JvmStatic
    fun <T : Boolean?> T.ifFalse(runnable: () -> Unit) {
        if (this == false) runnable()
    }

    @JvmStatic
    fun <T : Boolean?, R> T.ifTrue(supplier: () -> R?): R? = if (isTrue()) supplier() else null

    @JvmStatic
    fun <T : Boolean?, R> T.ifFalse(supplier: () -> R?): R? = if (isFalse()) supplier() else null

}
