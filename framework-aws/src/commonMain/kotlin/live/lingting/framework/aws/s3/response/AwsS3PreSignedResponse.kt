package live.lingting.framework.aws.s3.response

import kotlinx.serialization.Serializable

/**
 * @author lingting 2025/5/22 16:17
 */
@Serializable
class AwsS3PreSignedResponse(
    /**
     * 值必须为编码后的url
     */
    val url: String,
    val headers: Map<String, List<String>>
) {

    override fun toString(): String {
        return "[${headers.size}] $url"
    }

}
