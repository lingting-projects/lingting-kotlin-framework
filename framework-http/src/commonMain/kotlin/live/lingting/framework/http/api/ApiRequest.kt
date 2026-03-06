package live.lingting.framework.http.api

import io.ktor.http.HttpMethod
import io.ktor.http.supportsRequestBody
import kotlinx.serialization.Transient
import live.lingting.framework.http.body.EmptyBody
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.http.header.CollectionHttpHeaders
import live.lingting.framework.json.JsonExtraUtils.toJson
import live.lingting.framework.value.multi.StringMultiValue
import kotlin.jvm.JvmField

/**
 * @author lingting 2026/2/2 15:23
 */
abstract class ApiRequest {

    @JvmField
    @Transient
    val headers: live.lingting.framework.http.header.HttpHeaders =
        CollectionHttpHeaders()

    @JvmField
    @Transient
    val params: StringMultiValue = StringMultiValue()

    abstract fun method(): HttpMethod

    open fun contentType(): String? = null

    abstract fun path(): String

    open fun body(): live.lingting.framework.http.body.Body<*> {
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
