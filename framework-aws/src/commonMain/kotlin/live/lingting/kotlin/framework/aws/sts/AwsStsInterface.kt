package live.lingting.kotlin.framework.aws.sts

import live.lingting.kotlin.framework.aws.policy.Credential
import live.lingting.kotlin.framework.aws.policy.Statement
import live.lingting.kotlin.framework.util.DurationUtils.hours
import kotlin.time.Duration

/**
 * @author lingting 2025/6/3 16:02
 */
interface AwsStsInterface {

    val credentialExpire: Duration
        get() = 1.hours

    suspend fun credential(statement: Statement): Credential {
        return credential(setOf(statement))
    }

    suspend fun credential(statement: Statement, vararg statements: Statement): Credential {
        val list: MutableList<Statement> = ArrayList(statements.size + 1)
        list.add(statement)
        list.addAll(statements)
        return credential(list)
    }

    suspend fun credential(statements: Collection<Statement>): Credential {
        return credential(credentialExpire, statements)
    }

    suspend fun credential(timeout: Duration, statements: Collection<Statement>): Credential

}
