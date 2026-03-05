package live.lingting.kotlin.framework.aws.s3.response

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName

/**
 * @author lingting 2026/3/3 15:10
 */
@Serializable
@XmlSerialName("InitiateMultipartUploadResult")
data class AwsS3MultipartInitResponse(

    @XmlElement
    @XmlSerialName("Bucket")
    val bucket: String,

    @XmlElement
    @XmlSerialName("Key")
    val key: String,

    /**
     * 极其重要：后续分段上传和合并文件必须使用的 ID
     */
    @XmlElement
    @XmlSerialName("UploadId")
    val uploadId: String
)
