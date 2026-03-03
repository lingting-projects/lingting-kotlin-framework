package live.lingting.kotlin.framework.xml

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/3/3 14:18
 */
object XmlExtraUtils {

    @JvmStatic
    inline fun <reified T> T.toXml() = XmlUtils.toXml(this)

    @JvmStatic
    fun <T> T.toXml(serializer: SerializationStrategy<T>) = XmlUtils.toXml(serializer, this)

    @JvmStatic
    inline fun <reified T> String.xmlToObj() = XmlUtils.toObj<T>(this)

    @JvmStatic
    fun <T> String.xmlToObj(deserializer: DeserializationStrategy<T>) = XmlUtils.toObj(deserializer, this)

}
