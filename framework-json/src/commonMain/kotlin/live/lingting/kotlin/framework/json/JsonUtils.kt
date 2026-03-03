package live.lingting.kotlin.framework.json

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.internal.FormatLanguage
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/3/2 19:50
 */
object JsonUtils {

    var instance: Json = Json

    @JvmStatic
    inline fun <reified F, reified T> convert(f: F): T {
        if (f is T) {
            return f
        }
        if (f is String) {
            return toObj<T>(f)
        }
        val node = if (f is JsonElement) f else toNode(f)
        return toObj<T>(node)
    }

    @JvmStatic
    inline fun <reified T> toNode(t: T) = instance.encodeToJsonElement(t)

    @JvmStatic
    fun <T> toNode(serializer: SerializationStrategy<T>, t: T) = instance.encodeToJsonElement(serializer, t)

    @JvmStatic
    inline fun <reified T> toJson(t: T) = instance.encodeToString(t)

    @JvmStatic
    fun <T> toJson(serializer: SerializationStrategy<T>, t: T) = instance.encodeToString(serializer, t)

    @JvmStatic
    inline fun <reified T> toObj(@FormatLanguage("json", "", "") json: String) = instance.decodeFromString<T>(json)

    @JvmStatic
    fun <T> toObj(
        deserializer: DeserializationStrategy<T>,
        @FormatLanguage("json", "", "") json: String
    ): T = instance.decodeFromString(deserializer, json)

    @JvmStatic
    inline fun <reified T> toObj(json: JsonElement) = instance.decodeFromJsonElement<T>(json)

    @JvmStatic
    fun <T> toObj(
        deserializer: DeserializationStrategy<T>,
        json: JsonElement
    ): T = instance.decodeFromJsonElement(deserializer, json)

}
