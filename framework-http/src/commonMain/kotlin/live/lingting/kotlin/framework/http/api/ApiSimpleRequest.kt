package live.lingting.kotlin.framework.http.api

import io.ktor.http.HttpMethod
import live.lingting.kotlin.framework.http.body.Body
import live.lingting.kotlin.framework.http.body.EmptyBody
import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2024-09-19 15:41
 */
open class ApiSimpleRequest @JvmOverloads constructor(
    @JvmField
    protected val method: HttpMethod,
    @JvmField
    protected val path: String,
    @JvmField
    protected val body: Body<*> = EmptyBody
) : ApiRequest() {

    override fun method(): HttpMethod {
        return method
    }

    override fun path(): String {
        return path
    }

    override fun body(): Body<*> {
        return body
    }

}
