package live.lingting.kotlin.framework.aws.s3.response

import kotlinx.serialization.Serializable
import live.lingting.kotlin.framework.aws.s3.multipart.AwsS3MultipartItem
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * @author lingting 2026/3/3 11:43
 */
@Serializable
@XmlSerialName("ListMultipartUploadsResult", namespace = "http://s3.amazonaws.com/doc/2006-03-01/", prefix = "")
data class AwsS3MultipartListResponse(
    @XmlSerialName("Bucket")
    val bucket: String,
    @XmlSerialName("KeyMarker")
    val keyMarker: String? = null,
    @XmlSerialName("UploadIdMarker")
    val uploadIdMarker: String? = null,
    @XmlSerialName("NextKeyMarker")
    val nextKeyMarker: String? = null,
    @XmlSerialName("NextUploadIdMarker")
    val nextUploadIdMarker: String? = null,
    @XmlSerialName("MaxUploads")
    val maxUploads: Int,
    @XmlSerialName("IsTruncated")
    val isTruncated: Boolean,
    @XmlSerialName("Upload", namespace = "http://s3.amazonaws.com/doc/2006-03-01/", prefix = "")
    val uploads: List<MultipartUpload> = emptyList(),
    @XmlSerialName("CommonPrefixes", namespace = "http://s3.amazonaws.com/doc/2006-03-01/", prefix = "")
    val commonPrefixes: List<CommonPrefix>? = null
) {

    val items by lazy { items() }

    private fun items(): List<AwsS3MultipartItem> {
        return uploads.map { AwsS3MultipartItem(it.key, it.uploadId) }
    }

    @Serializable
    @XmlSerialName("Upload", namespace = "http://s3.amazonaws.com/doc/2006-03-01/", prefix = "")
    data class MultipartUpload(
        @XmlSerialName("Key")
        val key: String,
        @XmlSerialName("UploadId")
        val uploadId: String,
        @XmlSerialName("Initiator")
        val initiator: User? = null,
        @XmlSerialName("Owner")
        val owner: User? = null,
        @XmlSerialName("StorageClass")
        val storageClass: String,
        @XmlSerialName("Initiated")
        val initiated: String
    )

    @Serializable
    data class User(
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
