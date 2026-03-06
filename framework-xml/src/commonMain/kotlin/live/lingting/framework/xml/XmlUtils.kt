package live.lingting.framework.xml

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import nl.adaptivity.xmlutil.ExperimentalXmlUtilApi
import nl.adaptivity.xmlutil.XmlReader
import nl.adaptivity.xmlutil.serialization.DefaultXmlSerializationPolicy
import nl.adaptivity.xmlutil.serialization.FormatCache
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.defaultSharedFormatCache
import nl.adaptivity.xmlutil.xmlStreaming
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/3/3 14:18
 */
object XmlUtils {

    @OptIn(ExperimentalXmlUtilApi::class)
    var instance: XML = XML {
        repairNamespaces = true
        policy = DefaultXmlSerializationPolicy.Builder().apply {
            pedantic = false
            autoPolymorphic = true
            formatCache = runCatching { defaultSharedFormatCache() }.getOrElse { FormatCache.Dummy }
            ignoreUnknownChildren()
        }.build()
    }

    /**
     * @see XML.decodeFromString
     */
    @JvmStatic
    fun reader(xml: String): XmlReader {
        return when {
            instance.config.defaultToGenericParser -> xmlStreaming.newGenericReader(xml)
            else -> xmlStreaming.newReader(xml)
        }
    }

    @JvmStatic
    inline fun <reified T> toXml(t: T) = instance.encodeToString(t)

    @JvmStatic
    fun <T> toXml(serializer: SerializationStrategy<T>, t: T) = instance.encodeToString(serializer, t)

    @JvmStatic
    inline fun <reified T> toObj(xml: String) = instance.decodeFromString<T>(xml)

    @JvmStatic
    fun <T> toObj(
        deserializer: DeserializationStrategy<T>,
        xml: String
    ): T = instance.decodeFromString(deserializer, xml)

}
