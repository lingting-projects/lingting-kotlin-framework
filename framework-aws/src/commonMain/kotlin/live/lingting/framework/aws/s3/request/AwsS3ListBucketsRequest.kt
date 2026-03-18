package live.lingting.framework.aws.s3.request

import io.ktor.http.HttpMethod
import kotlinx.serialization.Serializable
import live.lingting.framework.aws.s3.AwsS3Request

/**
 * @author lingting 2025/1/14 19:57
 */
@Serializable
class AwsS3ListBucketsRequest : AwsS3Request() {

    /**
     * 指定区域
     */
    var region: String? = null

    var prefix: String? = null

    /**
     * continuationToken
     */
    var token: String? = null

    /**
     * 最大 10000
     */
    var maxBuckets: Int = 1000

    init {
        useBasicHost = true
    }

    override fun method(): HttpMethod {
        return HttpMethod.Get
    }

    override fun onBuildUrlBefore() {
        params.addIfPresent("region", region)
        params.addIfPresent("continuation-token", token)
        params.add("max-buckets", maxBuckets.toString())
        params.addIfPresent("prefix", prefix)
    }

    fun copy(): AwsS3ListBucketsRequest {
        val request = AwsS3ListBucketsRequest()
        request.region = region
        request.prefix = prefix
        request.token = token
        request.maxBuckets = maxBuckets
        return request
    }

}
