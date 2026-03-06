package live.lingting.framework.huawei.obs

import live.lingting.framework.aws.s3.AwsS3Object
import live.lingting.framework.aws.s3.interfaces.AwsS3ObjectDelegation
import live.lingting.framework.huawei.properties.HuaweiObsProperties

/**
 * @author lingting 2024-09-13 14:48
 */
class HuaweiObsObject(override val properties: HuaweiObsProperties, key: String) :
    HuaweiObs<AwsS3Object>(AwsS3Object(properties, key)), AwsS3ObjectDelegation {

    override suspend fun head(): HuaweiObsMeta {
        val head = super.head()
        return HuaweiObsMeta(head)
    }

}
