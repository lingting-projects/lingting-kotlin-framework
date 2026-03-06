package live.lingting.framework.huawei.iam

import kotlinx.coroutines.test.runTest
import live.lingting.framework.aws.policy.Statement
import live.lingting.framework.huawei.HuaweiBasic
import live.lingting.framework.huawei.properties.HuaweiIamProperties
import live.lingting.framework.util.CoroutineUtils
import live.lingting.framework.util.StringUtils.hasText
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HuaweiIamTest {

    var iam: HuaweiIam? = null

    var properties: HuaweiIamProperties? = null

    fun before() {
        iam = HuaweiBasic.iam()
        properties = iam!!.properties
    }

    @Test
    fun credential() = runTest {
        CoroutineUtils.switchScope(this)
        before()
        val statement = Statement(true)
        statement.addAction("obs:*")
        statement.addResource("obs:*:*:bucket:*")
        val credential = iam!!.credential(statement)
        assertNotNull(credential)
        assertTrue(hasText(credential.ak))
        assertTrue(hasText(credential.sk))
        assertTrue(hasText(credential.token))
        assertNotNull(credential.expire)
    }

}
