package live.lingting.kotlin.framework.aws.s3.enums

import kotlinx.serialization.Serializable
import live.lingting.kotlin.framework.serializable.EnumSerializer
import kotlin.jvm.JvmStatic


/**
 * @author lingting 2025/1/15 10:16
 */
@Serializable(with = StorageClass.Serializer::class)
enum class StorageClass {

    STANDARD,

    WARM,

    COLD,

    DEEP_ARCHIVE,

    ;

    companion object {

        @JvmStatic
        fun of(value: String?): StorageClass? {
            return entries.firstOrNull {
                if (value.isNullOrBlank()) {
                    false
                } else {
                    it.name == value || it.name.equals(value, ignoreCase = true)
                }
            }
        }
    }

    class Serializer : EnumSerializer<StorageClass>(enumValues<StorageClass>())

}
