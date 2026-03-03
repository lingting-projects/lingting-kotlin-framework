package live.lingting.kotlin.framework.http.api

import io.ktor.http.HttpMethod
import io.ktor.http.supportsRequestBody
import kotlinx.serialization.Transient
import live.lingting.kotlin.framework.http.QueryBuilder
import live.lingting.kotlin.framework.http.body.Body
import live.lingting.kotlin.framework.http.body.EmptyBody
import live.lingting.kotlin.framework.http.body.MemoryBody
import live.lingting.kotlin.framework.http.header.CollectionHttpHeaders
import live.lingting.kotlin.framework.http.header.HttpHeaders
import live.lingting.kotlin.framework.json.JsonExtraUtils.toJson
import kotlin.jvm.JvmField

/**
 * @author lingting 2026/2/2 15:23
 */
abstract class ApiRequest {

    @JvmField
    @Transient
    val headers: HttpHeaders = CollectionHttpHeaders()

    @JvmField
    @Transient
    val params: QueryBuilder = QueryBuilder()

    abstract fun method(): HttpMethod

    open fun contentType(): String? = null

    abstract fun path(): String

    open fun body(): Body<*> {
        if (!method().supportsRequestBody) {
            return EmptyBody
        }

        val json = toJson()
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
    open fun onBuildUrlBefore() {
        //
    }

}
