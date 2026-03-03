package live.lingting.kotlin.framework.aws.s3.request

import io.ktor.http.HttpMethod
import live.lingting.kotlin.framework.aws.s3.AwsS3Request
import live.lingting.kotlin.framework.http.body.Body
import live.lingting.kotlin.framework.http.body.EmptyBody

/**
 * @author lingting 2024-09-19 19:22
 */
open class AwsS3SimpleRequest(
    protected val method: HttpMethod,
    protected val body: Body<*>? = null
) : AwsS3Request() {

    override fun method(): HttpMethod {
        return method
    }

    override fun body(): Body<*> {
        if (body != null) {
            return body
        }
        return EmptyBody
    }

}
