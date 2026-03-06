package live.lingting.framework.huawei.obs

import kotlinx.coroutines.test.runTest
import live.lingting.framework.aws.properties.S3Properties
import live.lingting.framework.aws.s3.S3BasicTest
import live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation
import live.lingting.framework.aws.s3.interfaces.AwsS3ObjectDelegation
import live.lingting.framework.huawei.HuaweiBasic
import live.lingting.framework.huawei.iam.HuaweiIam
import live.lingting.framework.huawei.properties.HuaweiObsProperties
import live.lingting.framework.util.CoroutineUtils
import kotlin.test.Test

class HuaweiObsTest : S3BasicTest() {

    var iam: HuaweiIam? = null

    fun before() {
        iam = HuaweiBasic.iam()
    }

    @Test
    fun test() = runTest {
        CoroutineUtils.switchScope(this)
        before()
        run()
    }

    override suspend fun buildObj(key: String): AwsS3ObjectDelegation =
        iam!!.obsObject(properties as HuaweiObsProperties, key)

    override suspend fun buildBucket(): AwsS3BucketDelegation = iam!!.obsBucket(properties as HuaweiObsProperties)

    override fun properties(): S3Properties = HuaweiBasic.obsProperties()

}
