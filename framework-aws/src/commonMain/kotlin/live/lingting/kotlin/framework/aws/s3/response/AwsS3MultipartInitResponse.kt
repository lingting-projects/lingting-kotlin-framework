package live.lingting.kotlin.framework.aws.s3.response

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * @author lingting 2026/3/3 15:10
 */
@Serializable
@XmlSerialName("InitiateMultipartUploadResult", namespace = "http://s3.amazonaws.com/doc/2006-03-01/", prefix = "")
data class AwsS3MultipartInitResponse(
    @XmlSerialName("Bucket")
    val bucket: String,

    @XmlSerialName("Key")
    val key: String,

    /**
     * 极其重要：后续分段上传和合并文件必须使用的 ID
     */
    @XmlSerialName("UploadId")
    val uploadId: String
)
