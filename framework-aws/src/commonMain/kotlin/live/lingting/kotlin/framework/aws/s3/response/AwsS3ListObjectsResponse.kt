package live.lingting.kotlin.framework.aws.s3.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import live.lingting.kotlin.framework.aws.s3.request.AwsS3ListObjectsRequest
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * @author lingting 2025/1/14 20:07
 */
@Serializable
@XmlSerialName("ListBucketResult", namespace = "http://s3.amazonaws.com/doc/2006-03-01/", prefix = "")
data class AwsS3ListObjectsResponse(
    @XmlSerialName("Name")
    val name: String,
    @XmlSerialName("Prefix")
    val prefix: String? = null,
    @XmlSerialName("Delimiter")
    val delimiter: String? = null,
    @XmlSerialName("MaxKeys")
    val maxKeys: Int,
    @XmlSerialName("IsTruncated")
    val isTruncated: Boolean,
    @XmlSerialName("ContinuationToken")
    val continuationToken: String? = null,
    @XmlSerialName("NextContinuationToken")
    val nextContinuationToken: String? = null,
    @XmlSerialName("KeyCount")
    val keyCount: Int,
    @XmlSerialName("Contents", namespace = "http://s3.amazonaws.com/doc/2006-03-01/", prefix = "")
    val contents: List<Content> = emptyList(),
    @XmlSerialName("CommonPrefixes", namespace = "http://s3.amazonaws.com/doc/2006-03-01/", prefix = "")
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
    @XmlSerialName("Contents", namespace = "http://s3.amazonaws.com/doc/2006-03-01/", prefix = "")
    data class Content(
        @XmlSerialName("Key")
        val key: String,
        @XmlSerialName("LastModified")
        // 2023-10-27T10:00:00.000Z
        val lastModified: String,
        @XmlSerialName("ETag")
        val eTag: String,
        @XmlSerialName("Size")
        val size: Long,
        @XmlSerialName("StorageClass")
        val storageClass: String,
        @XmlSerialName("Owner")
        val owner: Owner? = null
    )

    @Serializable
    data class Owner(
        @XmlSerialName("ID")
        val id: String? = null,
        @XmlSerialName("DisplayName")
        val displayName: String? = null
    )

    @Serializable
    data class CommonPrefix(
        @XmlSerialName("Prefix")
        val prefix: String
    )
}
