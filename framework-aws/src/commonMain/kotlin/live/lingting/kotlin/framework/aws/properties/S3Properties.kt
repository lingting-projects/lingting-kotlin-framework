package live.lingting.kotlin.framework.aws.properties

import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.path
import live.lingting.kotlin.framework.aws.policy.Acl
import live.lingting.kotlin.framework.aws.policy.Credential
import live.lingting.kotlin.framework.aws.s3.enums.HostStyle

/**
 * @author lingting 2025/1/14 17:14
 */
abstract class S3Properties : AwsProperties() {

    open var hostStyle: HostStyle = HostStyle.VIRTUAL

    open var bucket: String = ""

    open var acl: Acl? = null

    open var domain: String? = null

    override fun host(): String {
        val str = domain
        if (str.isNullOrBlank()) {
            return buildHost()
        }
        return str
    }

    open fun buildHost(): String {
        if (hostStyle == HostStyle.VIRTUAL) {
            return virtualHost()
        }
        return secondHost()
    }

    open fun useCredential(credential: Credential) {
        ak = credential.ak
        sk = credential.sk
        token = credential.token
    }

    open fun from(properties: S3Properties) {
        ssl = properties.ssl
        region = properties.region
        endpoint = properties.endpoint
        bucket = properties.bucket
        acl = properties.acl
        domain = properties.domain
        ak = properties.ak
        sk = properties.sk
        token = properties.token
    }

    open fun copy(): S3Properties {
        return AwsS3Properties().also { it.from(this) }
    }

    open fun urlBuilder(): URLBuilder {
        val builder = URLBuilder()
        builder.host = host()

        if (ssl) {
            builder.protocol = URLProtocol.HTTPS
        } else {
            builder.protocol = URLProtocol.HTTP
        }

        if (hostStyle == HostStyle.SECOND) {
            builder.path(bucket)
        }

        return builder
    }

    open fun virtualHost(): String {
        val host = secondHost()
        if (bucket.isNotBlank()) {
            return "$bucket.$host"
        }
        return host
    }

    abstract fun secondHost(): String

}
