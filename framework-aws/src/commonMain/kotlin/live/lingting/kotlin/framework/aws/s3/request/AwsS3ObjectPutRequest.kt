package live.lingting.kotlin.framework.aws.s3.request

import io.ktor.http.HttpMethod
import live.lingting.kotlin.framework.aws.s3.AwsS3Request
import live.lingting.kotlin.framework.http.HttpContentTypes
import live.lingting.kotlin.framework.http.body.Body
import live.lingting.kotlin.framework.multipart.Part
import live.lingting.kotlin.framework.value.MultiValue
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2024-09-13 16:31
 */
open class AwsS3ObjectPutRequest(val body: Body<*>) : AwsS3Request() {

    companion object {

        @JvmStatic
        fun <C : Collection<String>> addMultipartParams(
            params: MultiValue<String, String, C>,
            uploadId: String?,
            part: Part?
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

    var part: Part? = null
        protected set

    fun multipart(id: String, part: Part) {
        this.uploadId = id
        this.part = part
    }

    override fun method(): HttpMethod {
        return HttpMethod.Put
    }

    override fun body(): Body<*> {
        return body
    }

    override fun onBuildBefore() {
        headers.contentType(HttpContentTypes.STREAM)
    }

    override fun onBuildUrlBefore() {
        if (part == null) {
            return
        }
        addMultipartParams(params, uploadId, part)
    }

}
