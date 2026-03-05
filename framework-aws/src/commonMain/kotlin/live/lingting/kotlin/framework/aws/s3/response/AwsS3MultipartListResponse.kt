package live.lingting.kotlin.framework.aws.s3.response

import kotlinx.serialization.Serializable
import live.lingting.kotlin.framework.aws.s3.multipart.AwsS3MultipartItem
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * @author lingting 2026/3/3 11:43
 */
@Serializable
@XmlSerialName("ListMultipartUploadsResult")
data class AwsS3MultipartListResponse(
    @XmlElement
    @XmlSerialName("Bucket")
    val bucket: String,
    @XmlElement
    @XmlSerialName("KeyMarker")
    val keyMarker: String? = null,
    @XmlElement
    @XmlSerialName("UploadIdMarker")
    val uploadIdMarker: String? = null,
    @XmlElement
    @XmlSerialName("NextKeyMarker")
    val nextKeyMarker: String? = null,
    @XmlElement
    @XmlSerialName("NextUploadIdMarker")
    val nextUploadIdMarker: String? = null,
    @XmlElement
    @XmlSerialName("MaxUploads")
    val maxUploads: Int,
    @XmlElement
    @XmlSerialName("IsTruncated")
    val isTruncated: Boolean,
    @XmlElement
    @XmlSerialName("Upload")
    val uploads: List<MultipartUpload> = emptyList(),
    @XmlElement
    @XmlSerialName("CommonPrefixes")
    val commonPrefixes: List<CommonPrefix>? = null
) {

    val items by lazy { items() }

    private fun items(): List<AwsS3MultipartItem> {
        return uploads.map { AwsS3MultipartItem(it.key, it.uploadId) }
    }

    @Serializable
    data class MultipartUpload(
        @XmlElement
        @XmlSerialName("Key")
        val key: String,
        @XmlElement
        @XmlSerialName("UploadId")
        val uploadId: String,
        @XmlElement
        @XmlSerialName("Initiator")
        val initiator: User? = null,
        @XmlElement
        @XmlSerialName("Owner")
        val owner: User? = null,
        @XmlElement
        @XmlSerialName("StorageClass")
        val storageClass: String,
        @XmlElement
        @XmlSerialName("Initiated")
        val initiated: String
    )

    @Serializable
    data class User(
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
