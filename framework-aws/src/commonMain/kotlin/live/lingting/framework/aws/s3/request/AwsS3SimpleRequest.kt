package live.lingting.framework.aws.s3.request

import io.ktor.http.HttpMethod

/**
 * @author lingting 2024-09-19 19:22
 */
open class AwsS3SimpleRequest(
    protected val method: HttpMethod,
    protected val body: live.lingting.framework.http.body.Body<*>? = null
) : live.lingting.framework.aws.s3.AwsS3Request() {

    override fun method(): HttpMethod {
        return method
    }

    override fun body(): live.lingting.framework.http.body.Body<*> {
        if (body != null) {
            return body
        }
        return live.lingting.framework.http.body.EmptyBody
    }

}
