package live.lingting.framework.aws.sts

import live.lingting.framework.util.DurationUtils.hours
import kotlin.time.Duration

/**
 * @author lingting 2025/6/3 16:02
 */
interface AwsStsInterface {

    val credentialExpire: Duration
        get() = 1.hours

    suspend fun credential(statement: live.lingting.framework.aws.policy.Statement): live.lingting.framework.aws.policy.Credential {
        return credential(setOf(statement))
    }

    suspend fun credential(
        statement: live.lingting.framework.aws.policy.Statement,
        vararg statements: live.lingting.framework.aws.policy.Statement
    ): live.lingting.framework.aws.policy.Credential {
        val list: MutableList<live.lingting.framework.aws.policy.Statement> = ArrayList(statements.size + 1)
        list.add(statement)
        list.addAll(statements)
        return credential(list)
    }

    suspend fun credential(statements: Collection<live.lingting.framework.aws.policy.Statement>): live.lingting.framework.aws.policy.Credential {
        return credential(credentialExpire, statements)
    }

    suspend fun credential(
        timeout: Duration,
        statements: Collection<live.lingting.framework.aws.policy.Statement>
    ): live.lingting.framework.aws.policy.Credential

}
