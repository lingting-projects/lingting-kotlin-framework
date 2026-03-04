package live.lingting.kotlin.framework.ali.sts

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import live.lingting.kotlin.framework.aws.policy.Statement
import live.lingting.kotlin.framework.http.body.Body
import live.lingting.kotlin.framework.http.body.MemoryBody
import live.lingting.kotlin.framework.json.JsonExtraUtils.toJson
import live.lingting.kotlin.framework.json.JsonExtraUtils.toJsonNode

/**
 * @author lingting 2024-09-14 13:45
 */
class AliStsCredentialRequest : AliStsRequest() {

    /**
     * 过期时长, 单位: 秒
     */
    var timeout: Long = 0

    var statements: Collection<Statement> = emptyList()

    var roleArn: String = ""

    var roleSessionName: String = ""

    override fun name(): String {
        return "AssumeRole"
    }

    override fun body(): Body<*> {
        val policy = buildJsonObject {
            put("Version", "1")
            put("Statement", statements.toJsonNode())
        }
        val map = buildJsonObject {
            put("RoleArn", roleArn)
            put("RoleSessionName", roleSessionName)
            put("DurationSeconds", timeout)
            put("Policy", policy)
        }
        val json = map.toJson()
        return MemoryBody(json)
    }

}
