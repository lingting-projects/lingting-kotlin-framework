package live.lingting.framework.aws.s3.enums

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmStatic


/**
 * @author lingting 2025/1/15 10:16
 */
@Serializable(with = _root_ide_package_.live.lingting.framework.aws.s3.enums.StorageClass.Serializer::class)
enum class StorageClass {

    STANDARD,

    WARM,

    COLD,

    DEEP_ARCHIVE,

    ;

    companion object {

        @JvmStatic
        fun of(value: String?): live.lingting.framework.aws.s3.enums.StorageClass? {
            return entries.firstOrNull {
                if (value.isNullOrBlank()) {
                    false
                } else {
                    it.name == value || it.name.equals(value, ignoreCase = true)
                }
            }
        }
    }

    class Serializer :
        live.lingting.framework.serializable.EnumSerializer<live.lingting.framework.aws.s3.enums.StorageClass>(
            enumValues<live.lingting.framework.aws.s3.enums.StorageClass>()
        )

}
