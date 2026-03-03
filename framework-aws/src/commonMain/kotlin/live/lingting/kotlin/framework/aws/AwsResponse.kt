package live.lingting.kotlin.framework.aws

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * @author lingting 2025/6/3 17:45
 */
// @JacksonXmlRootElement(localName = "AssumeRoleResponse", namespace = "https://sts.amazonaws.com/doc/2011-06-15/")
@Serializable
abstract class AwsResponse {

    @SerialName("ResponseMetadata")
    var metadata: Metadata = Metadata()

    @Serializable
    class Metadata {

        @SerialName("RequestId")
        var requestId = ""

    }

}
