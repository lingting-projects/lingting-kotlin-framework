package live.lingting.kotlin.framework.ali.oss

import live.lingting.kotlin.framework.ali.properties.AliOssProperties
import live.lingting.kotlin.framework.aws.s3.AwsS3Object
import live.lingting.kotlin.framework.aws.s3.interfaces.AwsS3ObjectDelegation

/**
 * @author lingting 2024-09-19 21:23
 */
class AliOssObject(properties: AliOssProperties, key: String) : AliOss<AwsS3Object>(AwsS3Object(properties, key)),
    AwsS3ObjectDelegation {

    override suspend fun head(): AliOssMeta {
        val head = super.head()
        return AliOssMeta(head)
    }

}
