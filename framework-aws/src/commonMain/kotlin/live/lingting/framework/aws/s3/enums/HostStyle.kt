package live.lingting.framework.aws.s3.enums

import kotlinx.serialization.Serializable
import live.lingting.framework.aws.s3.enums.HostStyle.Serializer
import live.lingting.framework.serializable.EnumSerializer

/**
 * @author lingting 2025/1/15 19:54
 */
@Serializable(with = Serializer::class)
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

    class Serializer : EnumSerializer<HostStyle>(enumValues<HostStyle>())

}
