package live.lingting.framework.aws.s3

import kotlinx.serialization.Transient
import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.aws.AwsUtils.HEADER_MD5
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.enums.HostStyle
import live.lingting.framework.aws.s3.impl.S3Meta
import live.lingting.framework.http.api.ApiRequest
import live.lingting.framework.http.body.Body
import live.lingting.framework.http.body.EmptyBody

/**
 * @author lingting 2024-09-19 15:03
 */
abstract class AwsS3Request : ApiRequest() {

    /**
     * 使用基础请求目标还是使用带桶的请求目标
     * @see HostStyle.VIRTUAL 不在域名拼接桶名
     * @see HostStyle.SECOND 不在url前面拼接桶名
     */
    @Transient
    var useBasicHost = false

    @Transient
    var key: String = ""

    @Transient
    var acl: Acl? = null

    /**
     * 自定义元数据key(不要前缀, 自动拼)
     */
    @Transient
    val meta: S3Meta = S3Meta.empty()

    override fun path(): String {
        return key
    }

    override fun body(): Body<*> {
        return EmptyBody
    }

    /**
     * 设置请求头上的 contentMd5 值
     */
    fun contentMd5(md5: String?) {
        if (md5.isNullOrBlank()) {
            headers.remove(HEADER_MD5)
        } else {
            headers.put(HEADER_MD5, md5)
        }
    }

    /**
     * 从原始的md5 生成请求头上的 contentMd5 值
     */
    fun contentMd5FromMd5(md5: String?) {
        var str = md5
        if (!str.isNullOrBlank()) {
            str = AwsUtils.contentMd5FromMd5(md5)
        }
        contentMd5(str)
    }

}
