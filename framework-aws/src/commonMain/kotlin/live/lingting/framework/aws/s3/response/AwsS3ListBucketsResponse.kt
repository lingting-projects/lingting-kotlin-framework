package live.lingting.framework.aws.s3.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import live.lingting.framework.aws.s3.request.AwsS3ListBucketsRequest
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * @author lingting 2025/1/14 20:07
 */
@Serializable
@SerialName("ListAllMyBucketsResult")
data class AwsS3ListBucketsResponse(
    @XmlElement
    @SerialName("Prefix")
    val prefix: String? = null,
    @XmlElement
    @SerialName("ContinuationToken")
    val continuationToken: String? = null,
    @XmlElement
    @SerialName("Buckets")
    @XmlSerialName("Buckets")
    private val bucketsWrapper: BucketsWrapper = BucketsWrapper(),
) {

    val buckets: List<Bucket> get() = bucketsWrapper.bucketList

    @Transient
    var nextRequest: AwsS3ListBucketsRequest? = null

    fun buildNextRequest(source: AwsS3ListBucketsRequest?): AwsS3ListBucketsRequest? {
        if (source == null || continuationToken.isNullOrBlank() || buckets.isEmpty()) {
            return null
        }
        val copy = source.copy()
        copy.token = continuationToken
        return copy
    }

    @Serializable
    data class BucketsWrapper(
        @SerialName("Bucket")
        @XmlSerialName("Bucket")
        val bucketList: List<Bucket> = emptyList()
    )

    @Serializable
    data class Bucket(
        @XmlElement
        @SerialName("Name")
        val name: String,
        @XmlElement
        @SerialName("Region")
        val region: String,
        @XmlElement
        @SerialName("StorageClass")
        val storageClass: String,
        @XmlElement
        @SerialName("CreationDate")
        // 2023-05-08T02:03:07.000Z
        val creationDate: String,
    )

}
