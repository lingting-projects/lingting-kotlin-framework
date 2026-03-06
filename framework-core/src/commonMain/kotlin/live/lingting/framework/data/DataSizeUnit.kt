package live.lingting.framework.data

import kotlin.jvm.JvmStatic

/**
 * @author lingting 2024/12/19 15:47
 */
enum class DataSizeUnit(
    val size: Long,
    val text: String,
) {
    BYTES(1, "Bytes"),
    KB(BYTES.size * _root_ide_package_.live.lingting.framework.data.DataSize.STEP, "KB"),
    MB(KB.size * _root_ide_package_.live.lingting.framework.data.DataSize.STEP, "MB"),
    GB(MB.size * _root_ide_package_.live.lingting.framework.data.DataSize.STEP, "GB"),
    TB(GB.size * _root_ide_package_.live.lingting.framework.data.DataSize.STEP, "TB"),
    PB(TB.size * _root_ide_package_.live.lingting.framework.data.DataSize.STEP, "PB"),

    ;

    companion object {

        @JvmStatic
        fun of(bit: Long): DataSizeUnit {
            return when {
                bit >= PB.size -> PB
                bit >= TB.size -> TB
                bit >= GB.size -> GB
                bit >= MB.size -> MB
                bit >= KB.size -> KB
                else -> BYTES
            }
        }
    }

    fun of(value: Long): live.lingting.framework.data.DataSize {
        return _root_ide_package_.live.lingting.framework.data.DataSize(value * size)
    }

    fun of(decimal: Double): live.lingting.framework.data.DataSize {
        val multiply = size * decimal
        return _root_ide_package_.live.lingting.framework.data.DataSize(multiply.toLong())
    }

}
