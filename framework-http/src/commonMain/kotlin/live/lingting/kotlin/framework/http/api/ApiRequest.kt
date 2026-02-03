package live.lingting.kotlin.framework.http.api

import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpMethod
import io.ktor.http.ParametersBuilder
import io.ktor.http.supportsRequestBody
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import live.lingting.kotlin.framework.http.body.Body
import live.lingting.kotlin.framework.http.body.EmptyBody
import live.lingting.kotlin.framework.http.body.MemoryBody
import kotlin.jvm.JvmField

/**
 * @author lingting 2026/2/2 15:23
 */
abstract class ApiRequest {

    @JvmField
    @Transient
    val headers = HeadersBuilder()

    @JvmField
    @Transient
    val params = ParametersBuilder()

    abstract fun method(): HttpMethod

    open fun contentType(): String? = null

    abstract fun path(): String

    open fun body(): Body<*> {
        if (!method().supportsRequestBody) {
            return EmptyBody
        }

        val json = Json.encodeToString(this)
        return MemoryBody(json)
    }

    /**
     * 在构造请求前触发
     */
    open fun onBuildBefore() {
        //
    }

    /**
     * 在构造Url前触发
     */
    open fun onUrlUrlBefore() {
        //
    }

}
