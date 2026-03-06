package live.lingting.framework.aws.s3

import io.ktor.http.HttpMethod
import live.lingting.framework.http.util.HttpExtraUtils.convert
import live.lingting.framework.xml.XmlExtraUtils.xmlToObj

/**
 * @author lingting 2024-09-19 15:09
 */
class AwsS3Bucket(properties: live.lingting.framework.aws.properties.S3Properties) : AwsS3(properties),
    live.lingting.framework.aws.s3.interfaces.AwsS3BucketInterface {
    override fun use(key: String): AwsS3Object {
        return AwsS3Object(properties, key)
    }

    override suspend fun multipartList(consumer: ((live.lingting.framework.aws.s3.request.AwsS3SimpleRequest) -> Unit)?): live.lingting.framework.aws.s3.response.AwsS3MultipartListResponse {
        val request = _root_ide_package_.live.lingting.framework.aws.s3.request.AwsS3SimpleRequest(HttpMethod.Get)
        consumer?.run { this(request) }
        request.params.add("uploads")
        return call(request).convert {
            it.xmlToObj<live.lingting.framework.aws.s3.response.AwsS3MultipartListResponse>()
        }
    }

    override suspend fun listObjects(request: live.lingting.framework.aws.s3.request.AwsS3ListObjectsRequest): live.lingting.framework.aws.s3.response.AwsS3ListObjectsResponse {
        return call(request).convert {
            val r = it.xmlToObj<live.lingting.framework.aws.s3.response.AwsS3ListObjectsResponse>()
            r.nextRequest = r.buildNextRequest(request)
            r
        }
    }

}
