package live.lingting.kotlin.framework.aws.s3

import kotlinx.coroutines.test.runTest
import live.lingting.kotlin.framework.aws.AwsBasic
import live.lingting.kotlin.framework.aws.properties.AwsS3Properties
import live.lingting.kotlin.framework.aws.properties.S3Properties
import live.lingting.kotlin.framework.aws.s3.interfaces.AwsS3BucketDelegation
import live.lingting.kotlin.framework.aws.s3.interfaces.AwsS3ObjectDelegation
import live.lingting.kotlin.framework.aws.sts.AwsStsClient
import kotlin.test.Test

/**
 * @author lingting 2026/3/3 15:11
 */
class AwsS3Test : S3BasicTest() {

    var sts: AwsStsClient? = null

    var useSts = false

    @Test
    fun test() = runTest {
        if (useSts) {
            sts = AwsBasic.sts()
        }
        run()
    }

    override suspend fun buildObj(key: String): AwsS3ObjectDelegation {
        val obj = if (useSts) sts!!.s3Object(properties as AwsS3Properties, key) else AwsS3Object(
            AwsBasic.s3Properties(),
            key
        )
        return object : AwsS3ObjectDelegation {
            override fun delegation(): AwsS3Object = obj
        }
    }

    override suspend fun buildBucket(): AwsS3BucketDelegation {
        val bucket = if (useSts) sts!!.s3Bucket(properties as AwsS3Properties) else AwsS3Bucket(AwsBasic.s3Properties())
        return object : AwsS3BucketDelegation {
            override fun delegation(): AwsS3Bucket = bucket
        }
    }

    override fun properties(): S3Properties {
        return if (useSts) AwsBasic.s3StsProperties() else AwsBasic.s3Properties()
    }

}
