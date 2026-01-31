package live.lingting.kotlin.framework.util

import kotlin.jvm.JvmStatic

/**
 * @author lingting
 */
object CharUtils {

    @JvmStatic
    fun Char.isLowerLetter(): Boolean {
        return isLowerCase()
    }

    @JvmStatic
    fun Char.isUpperLetter(): Boolean {
        return isUpperCase()
    }

    @JvmStatic
    fun Char.isLetter(): Boolean {
        return isLowerLetter() || isUpperLetter()
    }

}
