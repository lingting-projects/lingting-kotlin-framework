package live.lingting.kotlin.framework.ali

import io.ktor.http.HttpMethod
import kotlinx.serialization.Transient
import live.lingting.kotlin.framework.http.HttpContentTypes
import live.lingting.kotlin.framework.http.api.ApiRequest
import live.lingting.kotlin.framework.util.ValueUtils

/**
 * @author lingting 2024-09-14 13:49
 */
abstract class AliRequest : ApiRequest() {

    @Transient
    protected var nonceValue = ValueUtils.simpleUuid()

    abstract fun name(): String

    abstract fun version(): String

    override fun path(): String = ""

    fun nonce(): String {
        if (nonceValue.isBlank()) {
            nonceValue = ValueUtils.simpleUuid();
        }
        return nonceValue
    }

    override fun method(): HttpMethod {
        return HttpMethod.Post
    }

    override fun onBuildBefore() {
        headers.contentType(HttpContentTypes.JSON_UTF8)
    }

}
