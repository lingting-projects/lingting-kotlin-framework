package live.lingting.framework.aws.s3.request

import io.ktor.http.HttpMethod
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2024-09-13 16:31
 */
open class AwsS3ObjectPutRequest(val body: live.lingting.framework.http.body.Body<*>) :
    live.lingting.framework.aws.s3.AwsS3Request() {

    companion object {

        @JvmStatic
        fun <C : Collection<String>> addMultipartParams(
            params: live.lingting.framework.value.MultiValue<String, String, C>,
            uploadId: String?,
            part: live.lingting.framework.multipart.Part?
        ) {
            checkNotNull(part) { "content part must be not null!" }
            val u = uploadId
            params.add("partNumber", (part.index + 1).toString())
            checkNotNull(u) { "uploadId must be not null!" }
            params.add("uploadId", u)
        }

    }

    var uploadId: String? = null
        protected set

    var part: live.lingting.framework.multipart.Part? = null
        protected set

    fun multipart(id: String, part: live.lingting.framework.multipart.Part) {
        this.uploadId = id
        this.part = part
    }

    override fun method(): HttpMethod {
        return HttpMethod.Put
    }

    override fun body(): live.lingting.framework.http.body.Body<*> {
        return body
    }

    override fun onBuildBefore() {
        headers.contentType(live.lingting.framework.http.HttpContentTypes.STREAM)
    }

    override fun onBuildUrlBefore() {
        if (part == null) {
            return
        }
        addMultipartParams(params, uploadId, part)
    }

}
