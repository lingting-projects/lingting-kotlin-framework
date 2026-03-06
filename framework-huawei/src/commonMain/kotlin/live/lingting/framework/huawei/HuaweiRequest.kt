package live.lingting.framework.huawei

import io.ktor.http.HttpMethod
import live.lingting.framework.http.HttpContentTypes
import live.lingting.framework.http.api.ApiRequest

/**
 * @author lingting 2024-09-12 21:43
 */
abstract class HuaweiRequest : ApiRequest() {

    override fun method(): HttpMethod {
        return HttpMethod.Post
    }

    override fun onBuildBefore() {
        headers.contentType(HttpContentTypes.JSON_UTF8)
    }

}
