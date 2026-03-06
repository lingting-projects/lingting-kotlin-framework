package live.lingting.framework.aws.policy

import kotlinx.serialization.Serializable

/**
 * @author lingting 2024-09-12 20:55
 */
@Serializable(with = _root_ide_package_.live.lingting.framework.aws.policy.Acl.Serializer::class)
enum class Acl(override val value: String) : live.lingting.framework.value.KEnumValue<String> {

    /**
     * 不传递 acl, 继承桶策略
     */
    DEFAULT(""),

    PRIVATE("private"),

    PUBLIC_READ("public-read"),

    PUBLIC_READ_WRITE("public-read-write"),

    ;

    class Serializer :
        live.lingting.framework.serializable.EnumSerializer<live.lingting.framework.aws.policy.Acl>(enumValues<live.lingting.framework.aws.policy.Acl>())

}
