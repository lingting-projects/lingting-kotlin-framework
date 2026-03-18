package live.lingting.framework.aws.s3.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import live.lingting.framework.aws.s3.AwsS3Owner
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * @author lingting 2025/1/14 20:07
 */
@Serializable
@SerialName("ListBucketResult")
data class AwsS3ListObjectsResponse(
    @XmlElement
    @SerialName("Name")
    val name: String,
    @XmlElement
    @SerialName("Prefix")
    val prefix: String? = null,
    @XmlElement
    @SerialName("Delimiter")
    val delimiter: String? = null,
    @XmlElement
    @SerialName("MaxKeys")
    val maxKeys: Int,
    @XmlElement
    @SerialName("IsTruncated")
    val isTruncated: Boolean,
    @XmlElement
    @SerialName("ContinuationToken")
    val continuationToken: String? = null,
    /**
     * v1参数
     */
    @XmlElement
    @SerialName("NextMarker")
    val nextMarker: String? = null,
    /**
     * v2参数
     */
    @XmlElement
    @SerialName("NextContinuationToken")
    val nextContinuationToken: String? = null,
    @XmlElement
    @SerialName("KeyCount")
    private val count: Int? = null,
    @XmlElement
    @SerialName("Contents")
    @XmlSerialName("Contents")
    val contents: List<Content> = emptyList(),
    @XmlElement
    @SerialName("CommonPrefixes")
    val commonPrefixes: List<CommonPrefix>? = null
) {

    @Transient
    var nextRequest: live.lingting.framework.aws.s3.request.AwsS3ListObjectsRequest? = null

    val keyCount: Int
        get() = count ?: contents.size

    fun buildNextRequest(source: live.lingting.framework.aws.s3.request.AwsS3ListObjectsRequest?): live.lingting.framework.aws.s3.request.AwsS3ListObjectsRequest? {
        if (source == null || !isTruncated || (nextContinuationToken.isNullOrBlank() && nextMarker.isNullOrBlank())) {
            return null
        }
        val copy = source.copy()
        copy.token = if (source.v2) nextContinuationToken else nextMarker
        return copy
    }

    @Serializable
    data class Content(
        @XmlElement
        @SerialName("Key")
        val key: String,
        @XmlElement
        @SerialName("LastModified")
        // 2023-10-27T10:00:00.000Z
        val lastModified: String,
        @XmlElement
        @SerialName("ETag")
        val eTag: String,
        @XmlElement
        @SerialName("Size")
        val size: Long,
        @XmlElement
        @SerialName("StorageClass")
        val storageClass: String,
        @XmlElement
        @SerialName("Owner")
        val owner: AwsS3Owner? = null
    )

    @Serializable
    data class CommonPrefix(
        @XmlElement
        @SerialName("Prefix")
        val prefix: String
    )
}
