package live.lingting.framework.json

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/3/2 19:53
 */
object JsonExtraUtils {

    @JvmStatic
    fun JsonElement?.string(): String? {
        return this?.jsonPrimitive?.contentOrNull
    }

    @JvmStatic
    inline fun <reified F, reified T> F.jsonToObj() = JsonUtils.convert<F, T>(this)

    @JvmStatic
    inline fun <reified T> T.toJsonNode() = JsonUtils.toNode(this)

    @JvmStatic
    fun <T> T.toJsonNode(serializer: SerializationStrategy<T>) = JsonUtils.toNode(serializer, this)

    @JvmStatic
    inline fun <reified T> T.toJson() = JsonUtils.toJson(this)

    @JvmStatic
    fun <T> T.toJson(serializer: SerializationStrategy<T>) = JsonUtils.toJson(serializer, this)

    @JvmStatic
    inline fun <reified T> String.jsonToObj() = JsonUtils.toObj<T>(this)

    @JvmStatic
    fun <T> String.jsonToObj(deserializer: DeserializationStrategy<T>) = JsonUtils.toObj(deserializer, this)

    @JvmStatic
    inline fun <reified T> JsonElement.toObj() = JsonUtils.toObj<T>(this)

    @JvmStatic
    fun <T> JsonElement.toObj(deserializer: DeserializationStrategy<T>) = JsonUtils.toObj(deserializer, this)

}
