package live.lingting.kotlin.framework.aws.policy

import kotlinx.serialization.Serializable
import live.lingting.kotlin.framework.serializable.EnumSerializer
import live.lingting.kotlin.framework.value.KEnumValue

/**
 * @author lingting 2024-09-12 20:55
 */
@Serializable(with = Acl.Serializer::class)
enum class Acl(override val value: String) : KEnumValue<String> {

    /**
     * 不传递 acl, 继承桶策略
     */
    DEFAULT(""),

    PRIVATE("private"),

    PUBLIC_READ("public-read"),

    PUBLIC_READ_WRITE("public-read-write"),

    ;

    class Serializer : EnumSerializer<Acl>(enumValues<Acl>())

}
