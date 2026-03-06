package live.lingting.framework.huawei.obs

import live.lingting.framework.aws.s3.AwsS3Bucket
import live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation
import live.lingting.framework.aws.s3.request.AwsS3ListObjectsRequest
import live.lingting.framework.aws.s3.response.AwsS3ListObjectsResponse
import live.lingting.framework.huawei.properties.HuaweiObsProperties

/**
 * @author lingting 2024-09-13 14:48
 */
open class HuaweiObsBucket(override val properties: HuaweiObsProperties) :
    HuaweiObs<AwsS3Bucket>(AwsS3Bucket(properties)), AwsS3BucketDelegation {

    override fun use(key: String): HuaweiObsObject {
        return HuaweiObsObject(properties, key)
    }

    override suspend fun listObjects(request: AwsS3ListObjectsRequest): AwsS3ListObjectsResponse {
        request.v2 = false
        return super.listObjects(request)
    }

}
