package live.lingting.framework.aws.s3


import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray
import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.properties.S3Properties
import live.lingting.framework.aws.s3.impl.AwsS3DefaultListener
import live.lingting.framework.aws.s3.interfaces.AwsS3Listener
import live.lingting.framework.aws.s3.response.AwsS3PreSignedResponse
import live.lingting.framework.aws.signer.AwsSigner
import live.lingting.framework.http.DefaultHttpResponse
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.http.util.HttpUtils.isOk
import live.lingting.framework.json.JsonExtraUtils.toJson
import live.lingting.framework.time.DateTime
import live.lingting.framework.util.DurationUtils.isPositive

/**
 * @author lingting 2024-09-19 15:02
 */
abstract class AwsS3 protected constructor(val properties: S3Properties) :
    ApiClient<AwsS3Request>(properties.host()) {

    open val charset: Charset = Charsets.UTF_8

    val ak: String = properties.ak

    val sk: String = properties.sk

    val token: String? = properties.token

    val acl: Acl? = properties.acl

    val bucket: String = properties.bucket

    var listener: AwsS3Listener = AwsS3DefaultListener(this)

    override fun hostUrlBuilder(): URLBuilder {
        return super.hostUrlBuilder().also {
            it.protocol = if (properties.ssl) URLProtocol.HTTPS else URLProtocol.HTTP
        }
    }

    override suspend fun checkout(
        r: AwsS3Request,
        request: HttpRequestBuilder,
        response: HttpResponse
    ) {
        if (!response.isOk) {
            listener.onFailed(r, request, response)
        }
    }

    override suspend fun call(
        r: AwsS3Request,
        builder: HttpRequestBuilder
    ): HttpResponse {
        val signed = sign(r, builder)
        if (r !is AwsS3PreRequest) {
            return super.call(r, builder)
        }

        return callPre(builder, signed)
    }

    fun sign(
        r: AwsS3Request,
        builder: HttpRequestBuilder
    ): AwsSigner.Signed<out AwsSigner<*, out AwsSigner.Signed<*, *>>, *> {
        val headers = builder.headers
        if (r.acl != null && r.acl != Acl.DEFAULT) {
            headers[AwsUtils.HEADER_ACL] = r.acl!!.value
        }

        if (!token.isNullOrBlank()) {
            headers[AwsUtils.HEADER_TOKEN] = token
        }

        r.meta.forEach { k, vs ->
            val key = if (k.startsWith(AwsUtils.HEADER_PREFIX_META)) k else "${AwsUtils.HEADER_PREFIX_META}$k"
            headers.appendAll(key, vs)
        }

        val signer = listener.onSign(r, builder)

        val current = DateTime.current()

        val signed =
            if (r is AwsS3PreRequest) signer.signed(current, r.expire)
            else signer.signed(current)

        signed.replace(builder)
        return signed
    }

    private fun callPre(
        builder: HttpRequestBuilder,
        signed: AwsSigner.Signed<out AwsSigner<*, out AwsSigner.Signed<*, *>>, *>
    ): DefaultHttpResponse {
        val value = buildPre(builder, signed)
        val json = value.toJson()
        val bytes = json.toByteArray(charset)

        return DefaultHttpResponse.build {
            body(bytes)
        }
    }

    protected fun buildPre(
        builder: HttpRequestBuilder,
        signed: AwsSigner.Signed<out AwsSigner<*, out AwsSigner.Signed<*, *>>, *>
    ): AwsS3PreSignedResponse {
        val url = builder.url.buildString()
        return AwsS3PreSignedResponse(
            url,
            signed.headers.map().mapValues { (_, v) -> v.toList() })
    }

    open fun pre(r: AwsS3PreRequest): AwsS3PreSignedResponse {
        val expire = r.expire
        check(expire.isPositive) { "预签名过期时间不能为负数!" }
        val builder = buildRequest(r)
        val signed = sign(r, builder)
        return buildPre(builder, signed)
    }

}
