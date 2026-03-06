package live.lingting.framework.ali.oss

import live.lingting.framework.ali.properties.AliOssProperties
import live.lingting.framework.aws.s3.AwsS3Object
import live.lingting.framework.aws.s3.interfaces.AwsS3ObjectDelegation

/**
 * @author lingting 2024-09-19 21:23
 */
class AliOssObject(properties: live.lingting.framework.ali.properties.AliOssProperties, key: String) :
    live.lingting.framework.ali.oss.AliOss<live.lingting.framework.aws.s3.AwsS3Object>(
        live.lingting.framework.aws.s3.AwsS3Object(properties, key)
    ),
    live.lingting.framework.aws.s3.interfaces.AwsS3ObjectDelegation {

    override suspend fun head(): live.lingting.framework.ali.oss.AliOssMeta {
        val head = super.head()
        return _root_ide_package_.live.lingting.framework.ali.oss.AliOssMeta(head)
    }

}
