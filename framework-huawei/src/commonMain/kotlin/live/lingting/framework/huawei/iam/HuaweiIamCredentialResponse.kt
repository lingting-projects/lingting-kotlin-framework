package live.lingting.framework.huawei.iam

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * @author lingting 2024-09-13 14:00
 */
@Serializable
class HuaweiIamCredentialResponse {
    var credential: Credential = Credential()

    val access: String
        get() = credential.access

    val secret: String
        get() = credential.secret

    val securityToken: String
        get() = credential.securityToken

    val expire: String
        get() = credential.expire

    @Serializable
    class Credential {
        var access: String = ""

        var secret: String = ""

        @SerialName("securitytoken")
        var securityToken: String = ""

        @SerialName("expires_at")
        var expire: String = ""
    }
}
