package live.lingting.kotlin.framework.data

import kotlin.jvm.JvmStatic

/**
 * @author lingting 2024/12/19 15:47
 */
enum class DataSizeUnit(
    val size: Long,
    val text: String,
) {
    BYTES(1, "Bytes"),
    KB(BYTES.size * DataSize.STEP, "KB"),
    MB(KB.size * DataSize.STEP, "MB"),
    GB(MB.size * DataSize.STEP, "GB"),
    TB(GB.size * DataSize.STEP, "TB"),
    PB(TB.size * DataSize.STEP, "PB"),

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

    fun of(value: Long): DataSize {
        return DataSize(value * size)
    }

    fun of(decimal: Double): DataSize {
        val multiply = size * decimal
        return DataSize(multiply.toLong())
    }

}
