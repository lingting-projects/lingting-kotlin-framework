package live.lingting.kotlin.framework.aws.s3.request

import io.ktor.http.HttpMethod
import kotlinx.serialization.Serializable
import live.lingting.kotlin.framework.aws.s3.AwsS3Request
import live.lingting.kotlin.framework.util.BooleanUtils.ifTrue

/**
 * @author lingting 2025/1/14 19:57
 */
@Serializable
class AwsS3ListObjectsRequest : AwsS3Request() {

    var prefix: String? = null

    /**
     * v1: marker
     * v2: continuationToken
     */
    var token: String? = null

    var delimiter: String? = null

    /**
     * 最大 1000
     */
    var maxKeys: Int = 1000

    override fun method(): HttpMethod {
        return HttpMethod.Get
    }

    override fun onBuildUrlBefore() {
        params.add("list-type", "2")
        prefix?.run { isNotBlank().ifTrue { params.add("prefix", this) } }
        token?.run {
            isNotBlank().ifTrue {
                params.add("continuation-token", this)
            }
        }
        delimiter?.run { isNotBlank().ifTrue { params.add("delimiter", this) } }
        params.add("max-keys", maxKeys.toString())
    }

    fun copy(): AwsS3ListObjectsRequest {
        val request = AwsS3ListObjectsRequest()
        request.prefix = prefix
        request.token = token
        request.delimiter = delimiter
        request.maxKeys = maxKeys
        return request
    }

}
