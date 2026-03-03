package live.lingting.kotlin.framework.aws.sts

import kotlinx.serialization.Serializable
import live.lingting.kotlin.framework.aws.AwsResponse
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * @author lingting 2025/6/3 17:41
 */
@Serializable
@XmlSerialName("AssumeRoleResponse", namespace = "", prefix = "")
class AwsStsCredentialResponse : AwsResponse() {

    @XmlSerialName("AssumeRoleResult")
    var result: Result = Result()

    val accessKeyId: String
        get() = result.credentials.accessKeyId

    val secretAccessKey: String
        get() = result.credentials.secretAccessKey

    val sessionToken: String
        get() = result.credentials.sessionToken

    val expire: String
        get() = result.credentials.expiration

    @Serializable
    class Result {

        @XmlSerialName("Credentials")
        var credentials: Credentials = Credentials()

    }

    @Serializable
    class Credentials {

        @XmlSerialName("AccessKeyId")
        var accessKeyId: String = ""

        @XmlSerialName("SecretAccessKey")
        var secretAccessKey: String = ""

        @XmlSerialName("SessionToken")
        var sessionToken: String = ""

        @XmlSerialName("Expiration")
        var expiration: String = ""

    }

}
