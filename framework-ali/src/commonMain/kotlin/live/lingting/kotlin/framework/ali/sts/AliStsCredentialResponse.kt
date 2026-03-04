package live.lingting.kotlin.framework.ali.sts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import live.lingting.kotlin.framework.ali.AliResponse

/**
 * @author lingting 2024-09-14 13:50
 */
@Serializable
class AliStsCredentialResponse : AliResponse() {

    @SerialName("Credentials")
    var credentials: Credentials = Credentials()

    val accessKeyId: String
        get() = credentials.accessKeyId

    val accessKeySecret: String
        get() = credentials.accessKeySecret

    val securityToken: String
        get() = credentials.securityToken

    val expire: String
        get() = credentials.expiration

    @Serializable
    class Credentials {

        @SerialName("AccessKeyId")
        var accessKeyId: String = ""

        @SerialName("AccessKeySecret")
        var accessKeySecret: String = ""

        @SerialName("SecurityToken")
        var securityToken: String = ""

        @SerialName("Expiration")
        var expiration: String = ""

    }
}
