package live.lingting.framework.ali.sts

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import live.lingting.framework.json.JsonExtraUtils.toJson
import live.lingting.framework.json.JsonExtraUtils.toJsonNode

/**
 * @author lingting 2024-09-14 13:45
 */
class AliStsCredentialRequest : live.lingting.framework.ali.sts.AliStsRequest() {

    /**
     * 过期时长, 单位: 秒
     */
    var timeout: Long = 0

    var statements: Collection<live.lingting.framework.aws.policy.Statement> = emptyList()

    var roleArn: String = ""

    var roleSessionName: String = ""

    override fun name(): String {
        return "AssumeRole"
    }

    override fun body(): live.lingting.framework.http.body.Body<*> {
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
        return live.lingting.framework.http.body.MemoryBody(json)
    }

}
