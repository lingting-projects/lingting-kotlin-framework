package live.lingting.framework.aws.s3

import kotlinx.coroutines.test.runTest
import live.lingting.framework.aws.AwsBasic
import kotlin.test.Test

/**
 * @author lingting 2026/3/3 15:11
 */
class AwsS3Test : S3BasicTest() {

    var sts: live.lingting.framework.aws.sts.AwsSts? = null

    var useSts = false

    @Test
    fun test() = runTest {
        if (useSts) {
            sts = AwsBasic.sts()
        }
        run()
    }

    override suspend fun buildObj(key: String): live.lingting.framework.aws.s3.interfaces.AwsS3ObjectDelegation {
        val obj = if (useSts) sts!!.s3Object(
            properties as live.lingting.framework.aws.properties.AwsS3Properties,
            key
        ) else _root_ide_package_.live.lingting.framework.aws.s3.AwsS3Object(
            AwsBasic.s3Properties(),
            key
        )
        return object : live.lingting.framework.aws.s3.interfaces.AwsS3ObjectDelegation {
            override fun delegation(): live.lingting.framework.aws.s3.AwsS3Object = obj
        }
    }

    override suspend fun buildBucket(): live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation {
        val bucket =
            if (useSts) sts!!.s3Bucket(properties as live.lingting.framework.aws.properties.AwsS3Properties) else _root_ide_package_.live.lingting.framework.aws.s3.AwsS3Bucket(
                AwsBasic.s3Properties()
            )
        return object : live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation {
            override fun delegation(): live.lingting.framework.aws.s3.AwsS3Bucket = bucket
        }
    }

    override fun properties(): live.lingting.framework.aws.properties.S3Properties {
        return if (useSts) AwsBasic.s3StsProperties() else AwsBasic.s3Properties()
    }

}
