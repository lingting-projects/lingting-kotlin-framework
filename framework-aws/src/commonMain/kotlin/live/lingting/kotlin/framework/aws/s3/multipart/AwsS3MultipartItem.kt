package live.lingting.kotlin.framework.aws.s3.multipart

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmField

/**
 * @author lingting 2024-09-19 20:45
 */
@Serializable
data class AwsS3MultipartItem(@JvmField val key: String, @JvmField val uploadId: String)
