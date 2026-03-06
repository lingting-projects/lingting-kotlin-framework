package live.lingting.framework.aws.s3.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement

/**
 * @author lingting 2026/3/3 15:10
 */
@Serializable
@SerialName("InitiateMultipartUploadResult")
data class AwsS3MultipartInitResponse(

    @XmlElement
    @SerialName("Bucket")
    val bucket: String,

    @XmlElement
    @SerialName("Key")
    val key: String,

    /**
     * 极其重要：后续分段上传和合并文件必须使用的 ID
     */
    @XmlElement
    @SerialName("UploadId")
    val uploadId: String
)
