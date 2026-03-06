package live.lingting.framework.serializable

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import live.lingting.framework.util.EnumUtils
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/10 11:36
 */
abstract class EnumSerializer<E : Enum<E>> : KSerializer<E> {

    companion object {

        @JvmStatic
        fun <E : Enum<E>> buildSerialDescriptor(values: Array<E>, name: String?): SerialDescriptor {
            val first = values.first()
            val any = _root_ide_package_.live.lingting.framework.util.EnumUtils.getValue(first)
            requireNotNull(any) { "枚举值不允许为null!" }
            val cls = first::class
            val serialName = if (name.isNullOrBlank()) cls.qualifiedName ?: "UnknownEnum" else name

            val kind = when (any) {
                is Boolean -> PrimitiveKind.BOOLEAN
                is Int -> PrimitiveKind.INT
                is Long -> PrimitiveKind.LONG
                is Float -> PrimitiveKind.FLOAT
                is Double -> PrimitiveKind.DOUBLE
                is Short -> PrimitiveKind.SHORT
                is Byte -> PrimitiveKind.BYTE
                is Char -> PrimitiveKind.CHAR
                else -> PrimitiveKind.STRING
            }

            return PrimitiveSerialDescriptor(serialName, kind)
        }

    }

    val ignoreCase: Boolean

    val values: Array<E>

    val cls by lazy { values.first()::class }

    private val innerDescriptor: SerialDescriptor?

    override val descriptor: SerialDescriptor by lazy {
        innerDescriptor ?: buildSerialDescriptor(values, null)
    }

    @JvmOverloads
    constructor(values: Array<E>, descriptor: SerialDescriptor?, ignoreCase: Boolean = true) {
        this.values = values
        this.innerDescriptor = descriptor
        this.ignoreCase = ignoreCase
    }

    @JvmOverloads
    constructor(values: Array<E>, name: String?, ignoreCase: Boolean = true) : this(
        values,
        buildSerialDescriptor(values, name),
        ignoreCase
    )

    @JvmOverloads
    constructor(values: Array<E>, ignoreCase: Boolean = true) {
        this.values = values
        this.innerDescriptor = null
        this.ignoreCase = ignoreCase
    }

    override fun serialize(encoder: Encoder, value: E) {
        when (val v: Any? = _root_ide_package_.live.lingting.framework.util.EnumUtils.getValue(value)) {
            null -> encoder.encodeNull()
            is Number -> encoder.encodeLong(v.toLong())
            is Boolean -> encoder.encodeBoolean(v)
            else -> encoder.encodeString(v.toString())
        }
    }

    override fun deserialize(decoder: Decoder): E {
        val rawValue: Any = when (descriptor.kind) {
            PrimitiveKind.BOOLEAN -> decoder.decodeBoolean()
            PrimitiveKind.INT -> decoder.decodeInt()
            PrimitiveKind.LONG -> decoder.decodeLong()
            PrimitiveKind.FLOAT -> decoder.decodeFloat()
            PrimitiveKind.DOUBLE -> decoder.decodeDouble()
            PrimitiveKind.SHORT -> decoder.decodeShort()
            PrimitiveKind.BYTE -> decoder.decodeByte()
            PrimitiveKind.CHAR -> decoder.decodeChar()
            else -> decoder.decodeString()
        }

        val e = values
            .firstOrNull {
                val enumValue = EnumUtils.getValue(it) ?: return@firstOrNull false

                when (enumValue) {
                    rawValue -> true

                    is Number if rawValue is Number -> {
                        enumValue.toDouble() == rawValue.toDouble()
                    }

                    is Boolean if rawValue is Boolean -> {
                        enumValue == rawValue
                    }

                    else -> enumValue.toString().equals(rawValue.toString(), ignoreCase)
                }
            }
        return e ?: throw SerializationException("未知枚举值 '$rawValue'. 类: ${cls.qualifiedName}")
    }

}
