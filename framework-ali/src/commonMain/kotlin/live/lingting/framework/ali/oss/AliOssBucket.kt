package live.lingting.framework.ali.oss

import live.lingting.framework.ali.properties.AliOssProperties
import live.lingting.framework.aws.s3.AwsS3Bucket
import live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation

/**
 * @author lingting 2024-09-19 21:21
 */
open class AliOssBucket(
    protected val ossProperties: AliOssProperties
) : AliOss<AwsS3Bucket>(
    AwsS3Bucket(
        ossProperties
    )
), AwsS3BucketDelegation {

    override fun use(key: String): AliOssObject {
        return AliOssObject(ossProperties, key)
    }

}
