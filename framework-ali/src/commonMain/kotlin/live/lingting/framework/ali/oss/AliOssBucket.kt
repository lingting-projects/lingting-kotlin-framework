package live.lingting.framework.ali.oss

import live.lingting.framework.ali.properties.AliOssProperties
import live.lingting.framework.aws.s3.AwsS3Bucket
import live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation

/**
 * @author lingting 2024-09-19 21:21
 */
open class AliOssBucket(
    protected val ossProperties: live.lingting.framework.ali.properties.AliOssProperties
) : live.lingting.framework.ali.oss.AliOss<live.lingting.framework.aws.s3.AwsS3Bucket>(
    live.lingting.framework.aws.s3.AwsS3Bucket(
        ossProperties
    )
), live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation {

    override fun use(key: String): live.lingting.framework.ali.oss.AliOssObject {
        return _root_ide_package_.live.lingting.framework.ali.oss.AliOssObject(ossProperties, key)
    }

}
