package live.lingting.framework.huawei.iam

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.json.JsonExtraUtils.toJson
import live.lingting.framework.json.JsonExtraUtils.toJsonNode
import kotlin.jvm.JvmField

/**
 * @author lingting 2024-09-12 21:38
 */
class HuaweiIamTokenRequest : HuaweiIamRequest() {

    companion object {

        const val KEY_PASSWORD: String = "password"

        @JvmField
        val VALUE_METHODS: Array<String> = arrayOf(KEY_PASSWORD)

    }

    var domain: Map<String, JsonElement>? = null

    var username: String? = null

    var password: String? = null

    override fun usingToken(): Boolean {
        return false
    }

    override fun path(): String {
        return "v3/auth/tokens"
    }

    override fun body(): MemoryBody {
        val map = buildJsonObject {
            put("auth", buildJsonObject {
                put("identity", buildJsonObject {
                    put("methods", VALUE_METHODS.toJsonNode())
                    put(KEY_PASSWORD, buildJsonObject {
                        put("user", buildJsonObject {
                            domain?.also { put("domain", it.toJsonNode()) }
                            put("name", username)
                            put(KEY_PASSWORD, password)
                        })
                    })
                })
            })
        }
        val json = map.toJson()
        return MemoryBody(json)
    }

}
