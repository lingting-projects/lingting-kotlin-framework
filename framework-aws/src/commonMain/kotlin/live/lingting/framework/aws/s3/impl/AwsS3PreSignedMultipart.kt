package live.lingting.framework.aws.s3.impl

/**
 * @author lingting 2025/7/8 10:07
 */
class AwsS3PreSignedMultipart(
    /**
     * 文件大小
     */
    val size: live.lingting.framework.data.DataSize,
    /**
     * 上传id
     */
    val uploadId: String,
    /**
     * 分片大小
     */
    val partSize: live.lingting.framework.data.DataSize,
    /**
     * 每片的预签名
     */
    val items: List<Item>,
) {

    class Item(
        val part: live.lingting.framework.multipart.Part,
        val url: String,
        val headers: Map<String, List<String>>
    ) {}

}
