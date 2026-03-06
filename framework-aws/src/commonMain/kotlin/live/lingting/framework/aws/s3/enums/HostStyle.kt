package live.lingting.framework.aws.s3.enums

import kotlinx.serialization.Serializable

/**
 * @author lingting 2025/1/15 19:54
 */
@Serializable(with = _root_ide_package_.live.lingting.framework.aws.s3.enums.HostStyle.Serializer::class)
enum class HostStyle {

    /**
     * https://s3.region.amazonaws.com/s3-bucket
     */
    SECOND,

    /**
     * https://s3-bucket.s3.region.amazonaws.com
     */
    VIRTUAL,

    ;

    class Serializer :
        live.lingting.framework.serializable.EnumSerializer<live.lingting.framework.aws.s3.enums.HostStyle>(enumValues<live.lingting.framework.aws.s3.enums.HostStyle>())

}
