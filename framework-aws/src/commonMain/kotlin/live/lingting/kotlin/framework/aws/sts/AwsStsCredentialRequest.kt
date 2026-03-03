package live.lingting.kotlin.framework.aws.sts

import live.lingting.kotlin.framework.aws.policy.Statement
import live.lingting.kotlin.framework.json.JsonExtraUtils.toJson
import live.lingting.kotlin.framework.util.DurationUtils.isPositive
import kotlin.time.Duration

/**
 * @author lingting 2025/6/3 14:45
 */
class AwsStsCredentialRequest : AwsStsRequest() {

    /**
     * 过期时长, 单位: 秒
     */
    var timeout: Duration = Duration.ZERO

    var statements: Collection<Statement> = emptyList()

    var roleArn: String = ""

    var roleSessionName: String = ""

    var sourceIdentity: String = ""

    override fun action(): String = "AssumeRole"

    override fun onBuildBefore() {
        check(timeout.isPositive) { "timeout must be is positive" }
        check(roleArn.isNotBlank()) { "roleArn must be is not blank" }
        check(roleSessionName.isNotBlank()) { "roleSessionName must be is not blank" }
        super.onBuildBefore()
    }

    override fun onBuildUrlBefore() {
        params.add("DurationSeconds", timeout.inWholeSeconds.toString())
        params.add("RoleArn", roleArn)
        params.add("RoleSessionName", roleSessionName)
        if (sourceIdentity.isNotBlank()) {
            params.add("SourceIdentity", sourceIdentity)
        }
        val policy = buildMap {
            put("Version", "2012-10-17")
            put("Statement", statements.map { it.map() })
        }
        params.add("Policy", policy.toJson())
    }

}
