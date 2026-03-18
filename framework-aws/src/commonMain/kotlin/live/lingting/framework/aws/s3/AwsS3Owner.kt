package live.lingting.framework.aws.s3

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement

/**
 * @author lingting 2026/3/18 11:23
 */
@Serializable
data class AwsS3Owner(
    @XmlElement
    @SerialName("ID")
    val id: String? = null,
    @XmlElement
    @SerialName("DisplayName")
    val displayName: String? = null
) {
}
