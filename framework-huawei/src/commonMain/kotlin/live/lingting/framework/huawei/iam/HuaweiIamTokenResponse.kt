package live.lingting.framework.huawei.iam

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author lingting 2024-09-13 11:52
 */
@Serializable
class HuaweiIamTokenResponse {
    var token: Token = Token()

    val expire: String
        get() = token.expire

    val issued: String
        get() = token.issued

    @Serializable
    class Token {
        @SerialName("expires_at")
        var expire: String = ""

        @SerialName("issued_at")
        var issued: String = ""
    }
}
