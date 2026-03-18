package live.lingting.framework.aws.s3.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import live.lingting.framework.aws.s3.AwsS3Owner
import live.lingting.framework.aws.s3.multipart.AwsS3MultipartItem
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * @author lingting 2026/3/3 11:43
 */
@Serializable
@SerialName("ListMultipartUploadsResult")
data class AwsS3MultipartListResponse(
    @XmlElement
    @SerialName("Bucket")
    val bucket: String,
    @XmlElement
    @SerialName("KeyMarker")
    val keyMarker: String? = null,
    @XmlElement
    @SerialName("UploadIdMarker")
    val uploadIdMarker: String? = null,
    @XmlElement
    @SerialName("NextKeyMarker")
    val nextKeyMarker: String? = null,
    @XmlElement
    @SerialName("NextUploadIdMarker")
    val nextUploadIdMarker: String? = null,
    @XmlElement
    @SerialName("MaxUploads")
    val maxUploads: Int,
    @XmlElement
    @SerialName("IsTruncated")
    val isTruncated: Boolean,
    @XmlElement
    @SerialName("Upload")
    @XmlSerialName("Upload")
    val uploads: List<MultipartUpload> = emptyList(),
    @XmlElement
    @SerialName("CommonPrefixes")
    val commonPrefixes: List<CommonPrefix>? = null
) {

    val items by lazy { items() }

    private fun items(): List<AwsS3MultipartItem> {
        return uploads.map {
            AwsS3MultipartItem(
                it.key,
                it.uploadId
            )
        }
    }

    @Serializable
    data class MultipartUpload(
        @XmlElement
        @SerialName("Key")
        val key: String,
        @XmlElement
        @SerialName("UploadId")
        val uploadId: String,
        @XmlElement
        @SerialName("Initiator")
        @XmlSerialName("Initiator")
        val initiator: AwsS3Owner? = null,
        @XmlElement
        @SerialName("Owner")
        @XmlSerialName("Owner")
        val owner: AwsS3Owner? = null,
        @XmlElement
        @SerialName("StorageClass")
        val storageClass: String,
        @XmlElement
        @SerialName("Initiated")
        val initiated: String
    )

    @Serializable
    data class CommonPrefix(
        @XmlElement
        @SerialName("Prefix")
        val prefix: String
    )
}
