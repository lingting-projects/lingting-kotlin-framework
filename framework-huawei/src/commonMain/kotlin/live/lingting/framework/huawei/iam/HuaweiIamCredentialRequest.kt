package live.lingting.framework.huawei.iam

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import live.lingting.framework.aws.policy.Statement
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.huawei.HuaweiUtils.CREDENTIAL_EXPIRE
import live.lingting.framework.json.JsonExtraUtils.toJson
import live.lingting.framework.json.JsonExtraUtils.toJsonNode
import kotlin.jvm.JvmField
import kotlin.time.Duration

/**
 * @author lingting 2024-09-13 13:53
 */
class HuaweiIamCredentialRequest : HuaweiIamRequest() {

    companion object {
        @JvmField
        val VALUE_METHODS: Array<String> = arrayOf("token")
    }

    var timeout: Duration = CREDENTIAL_EXPIRE

    var statements: Collection<Statement> = emptyList()

    override fun path(): String {
        return "v3.0/OS-CREDENTIAL/securitytokens"
    }

    override fun body(): live.lingting.framework.http.body.Body<*> {
        val map = buildJsonObject {
            put("auth", buildJsonObject {
                put("identity", buildJsonObject {
                    put("methods", VALUE_METHODS.toJsonNode())
                    put("token", buildJsonObject {
                        put("duration_seconds", timeout.inWholeSeconds)
                    })
                    put("policy", buildJsonObject {
                        put("Version", "1.1")
                        put("Statement", statements.toJsonNode())
                    })
                })
            })
        }
        val json = map.toJson()
        return MemoryBody(json)
    }

}
