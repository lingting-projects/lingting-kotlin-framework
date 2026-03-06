package live.lingting.framework.ali.sts

import kotlinx.coroutines.test.runTest
import live.lingting.framework.ali.AliBasic
import live.lingting.framework.aws.policy.Statement
import live.lingting.framework.util.StringUtils
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * @author lingting 2024-09-14 18:15
 */
internal class AliStsTest {

    var sts: AliSts? = null

    @Test
    fun credential() = runTest {
        sts = AliBasic.sts()
        val statement = Statement(true)
        statement.addAction("obs:*")
        statement.addResource("obs:*:*:bucket:*")
        val credential = sts!!.credential(statement)
        assertNotNull(credential)
        assertTrue(StringUtils.hasText(credential.ak))
        assertTrue(StringUtils.hasText(credential.sk))
        assertTrue(StringUtils.hasText(credential.token))
        assertNotNull(credential.expire)
    }
}
