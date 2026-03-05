package live.lingting.kotlin.framework.aws.s3.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import live.lingting.kotlin.framework.aws.s3.request.AwsS3ListObjectsRequest
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * @author lingting 2025/1/14 20:07
 */
@Serializable
@XmlSerialName("ListBucketResult")
data class AwsS3ListObjectsResponse(
    @XmlElement
    @XmlSerialName("Name")
    val name: String,
    @XmlElement
    @XmlSerialName("Prefix")
    val prefix: String? = null,
    @XmlElement
    @XmlSerialName("Delimiter")
    val delimiter: String? = null,
    @XmlElement
    @XmlSerialName("MaxKeys")
    val maxKeys: Int,
    @XmlElement
    @XmlSerialName("IsTruncated")
    val isTruncated: Boolean,
    @XmlElement
    @XmlSerialName("ContinuationToken")
    val continuationToken: String? = null,
    @XmlElement
    @XmlSerialName("NextContinuationToken")
    val nextContinuationToken: String? = null,
    @XmlElement
    @XmlSerialName("KeyCount")
    val keyCount: Int,
    @XmlElement
    @XmlSerialName("Contents")
    val contents: List<Content> = emptyList(),
    @XmlElement
    @XmlSerialName("CommonPrefixes")
    val commonPrefixes: List<CommonPrefix>? = null
) {

    @Transient
    var nextRequest: AwsS3ListObjectsRequest? = null

    fun buildNextRequest(source: AwsS3ListObjectsRequest?): AwsS3ListObjectsRequest? {
        if (source == null || !isTruncated || nextContinuationToken.isNullOrBlank()) {
            return null
        }
        val copy = source.copy()
        copy.token = nextContinuationToken
        return copy
    }

    @Serializable
    data class Content(
        @XmlElement
        @XmlSerialName("Key")
        val key: String,
        @XmlElement
        @XmlSerialName("LastModified")
        // 2023-10-27T10:00:00.000Z
        val lastModified: String,
        @XmlElement
        @XmlSerialName("ETag")
        val eTag: String,
        @XmlElement
        @XmlSerialName("Size")
        val size: Long,
        @XmlElement
        @XmlSerialName("StorageClass")
        val storageClass: String,
        @XmlElement
        @XmlSerialName("Owner")
        val owner: Owner? = null
    )

    @Serializable
    data class Owner(
        @XmlElement
        @XmlSerialName("ID")
        val id: String? = null,
        @XmlElement
        @XmlSerialName("DisplayName")
        val displayName: String? = null
    )

    @Serializable
    data class CommonPrefix(
        @XmlElement
        @XmlSerialName("Prefix")
        val prefix: String
    )
}
