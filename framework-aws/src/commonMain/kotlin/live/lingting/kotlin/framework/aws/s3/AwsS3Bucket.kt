package live.lingting.kotlin.framework.aws.s3

import io.ktor.http.HttpMethod
import live.lingting.kotlin.framework.aws.AwsS3Client
import live.lingting.kotlin.framework.aws.properties.S3Properties
import live.lingting.kotlin.framework.aws.s3.interfaces.AwsS3BucketInterface
import live.lingting.kotlin.framework.aws.s3.request.AwsS3ListObjectsRequest
import live.lingting.kotlin.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.kotlin.framework.aws.s3.response.AwsS3ListObjectsResponse
import live.lingting.kotlin.framework.aws.s3.response.AwsS3MultipartListResponse
import live.lingting.kotlin.framework.http.util.HttpExtraUtils.convert
import live.lingting.kotlin.framework.xml.XmlExtraUtils.xmlToObj

/**
 * @author lingting 2024-09-19 15:09
 */
class AwsS3Bucket(properties: S3Properties) : AwsS3Client(properties), AwsS3BucketInterface {
    override fun use(key: String): AwsS3Object {
        return AwsS3Object(properties, key)
    }

    override suspend fun multipartList(consumer: ((AwsS3SimpleRequest) -> Unit)?): AwsS3MultipartListResponse {
        val request = AwsS3SimpleRequest(HttpMethod.Get)
        consumer?.run { this(request) }
        request.params.add("uploads")
        return call(request).convert {
            it.xmlToObj<AwsS3MultipartListResponse>()
        }
    }

    override suspend fun listObjects(request: AwsS3ListObjectsRequest): AwsS3ListObjectsResponse {
        return call(request).convert {
            val r = it.xmlToObj<AwsS3ListObjectsResponse>()
            r.nextRequest = r.buildNextRequest(request)
            r
        }
    }

}
