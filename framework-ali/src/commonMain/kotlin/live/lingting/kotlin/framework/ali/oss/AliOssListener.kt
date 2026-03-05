package live.lingting.kotlin.framework.ali.oss

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodedPath
import live.lingting.kotlin.framework.ali.AliUtils
import live.lingting.kotlin.framework.ali.exception.AliOssException
import live.lingting.kotlin.framework.ali.signer.AliV4Signer
import live.lingting.kotlin.framework.aws.AwsUtils
import live.lingting.kotlin.framework.aws.s3.AwsS3Client
import live.lingting.kotlin.framework.aws.s3.AwsS3Request
import live.lingting.kotlin.framework.aws.s3.enums.HostStyle
import live.lingting.kotlin.framework.aws.s3.impl.AwsS3DefaultListener
import live.lingting.kotlin.framework.aws.signer.AwsSigner
import live.lingting.kotlin.framework.http.util.HttpHeadersUtils.to
import live.lingting.kotlin.framework.http.util.HttpUrlUtils.buildPath
import live.lingting.kotlin.framework.http.util.ParametersUtils.to
import live.lingting.kotlin.framework.util.Base64Utils.toBase64Bytes
import live.lingting.kotlin.framework.util.StringUtils

/**
 * @author lingting 2024/11/5 14:53
 */
class AliOssListener(client: AwsS3Client) : AwsS3DefaultListener(client) {

    override suspend fun onFailed(r: AwsS3Request, request: HttpRequestBuilder, response: HttpResponse) {
        val headers = response.headers.to()
        val ec = headers.first(AliUtils.HEADER_EC, "")

        var string = response.bodyAsText()

        if (!StringUtils.hasText(string)) {
            val err = headers.first(AliUtils.HEADER_ERR, "")
            if (StringUtils.hasText(err)) {
                val base64 = err.toBase64Bytes()
                string = base64.decodeToString()
            }
        }

        val httpStatus = response.status.value
        val msg = "[OSS] ALI OSS请求异常! uri: ${request.url.encodedPath}; httpStatus: $httpStatus; ec: \n$ec"
        log.error {
            "$msg body: \n$string"
        }
        throw AliOssException(msg)
    }

    override fun onSign(r: AwsS3Request, builder: HttpRequestBuilder): AwsSigner<*, *> {
        val properties = client.properties

        val headers = builder.headers.to()
        headers.keys().forEach { name ->
            if (name == AwsUtils.HEADER_ACL) {
                headers.replace(name, AliUtils.HEADER_ACL)
            } else if (name.startsWith(AwsUtils.HEADER_PREFIX)) {
                val newName = name.replace(AwsUtils.HEADER_PREFIX, AliUtils.HEADER_PREFIX)
                headers.replace(name, newName)
            }
        }

        val url = builder.url
        val path = if (properties.hostStyle != HostStyle.VIRTUAL) url.buildPath()
        else "/${properties.bucket}${url.buildPath()}"

        return AliV4Signer(
            builder.method,
            path,
            headers,
            null,
            url.parameters.to(),
            properties.region,
            properties.ak,
            properties.sk,
            "oss"
        )
    }

}
