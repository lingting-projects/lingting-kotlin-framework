package live.lingting.kotlin.framework.util

import live.lingting.kotlin.framework.value.KEnumValue
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2022/12/20 14:52
 */
object EnumUtils {

    @JvmField
    val CACHE = mutableMapOf<Enum<*>, Any>()

    @JvmStatic
    fun <E : Enum<E>> getValue(e: Enum<E>?): Any? {
        if (e == null) {
            return null
        }
        val cv = CACHE[e]
        if (cv != null) {
            return cv;
        }
        var v: Any? = null
        if (e is KEnumValue<*>) {
            v = e.value
        }

        if (v == null) {
            v = internalGetValue(e)
        }
        if (v == null) {
            v = e.name
        }
        CACHE[e] = v
        return v
    }

}

internal expect fun <E : Enum<E>> EnumUtils.internalGetValue(e: Enum<E>): Any?

