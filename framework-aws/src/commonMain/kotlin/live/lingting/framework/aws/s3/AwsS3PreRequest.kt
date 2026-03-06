package live.lingting.framework.aws.s3

import io.ktor.http.HttpMethod
import kotlinx.serialization.Transient
import live.lingting.framework.aws.s3.request.AwsS3ObjectPutRequest.Companion.addMultipartParams
import live.lingting.framework.util.DurationUtils.hours
import kotlin.time.Duration

/**
 * @author lingting 2025/6/3 20:01
 */
class AwsS3PreRequest(method: HttpMethod, body: live.lingting.framework.http.body.Body<*>? = null) :
    live.lingting.framework.aws.s3.request.AwsS3SimpleRequest(method, body) {

    /**
     * 指定过期时长
     */
    @Transient
    var expire: Duration = 24.hours

    fun multipart(uploadId: String?, part: live.lingting.framework.multipart.Part?) {
        addMultipartParams(
            params,
            uploadId,
            part
        )
    }

}
