package live.lingting.kotlin.framework.aws.s3

import io.ktor.http.HttpMethod
import kotlinx.serialization.Transient
import live.lingting.kotlin.framework.aws.s3.request.AwsS3ObjectPutRequest.Companion.addMultipartParams
import live.lingting.kotlin.framework.aws.s3.request.AwsS3SimpleRequest
import live.lingting.kotlin.framework.http.body.Body
import live.lingting.kotlin.framework.multipart.Part
import live.lingting.kotlin.framework.util.DurationUtils.hours
import kotlin.time.Duration

/**
 * @author lingting 2025/6/3 20:01
 */
class AwsS3PreRequest(method: HttpMethod, body: Body<*>? = null) : AwsS3SimpleRequest(method, body) {

    /**
     * 指定过期时长
     */
    @Transient
    var expire: Duration = 24.hours

    fun multipart(uploadId: String?, part: Part?) {
        addMultipartParams(params, uploadId, part)
    }

}
