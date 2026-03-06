package live.lingting.framework.aws.sts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author lingting 2025/6/3 17:41
 */
@Serializable
@SerialName("AssumeRoleResponse")
class AwsStsCredentialResponse : live.lingting.framework.aws.AwsResponse() {

    @SerialName("AssumeRoleResult")
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

        @SerialName("Credentials")
        var credentials: Credentials = Credentials()

    }

    @Serializable
    class Credentials {

        @SerialName("AccessKeyId")
        var accessKeyId: String = ""

        @SerialName("SecretAccessKey")
        var secretAccessKey: String = ""

        @SerialName("SessionToken")
        var sessionToken: String = ""

        @SerialName("Expiration")
        var expiration: String = ""

    }

}
