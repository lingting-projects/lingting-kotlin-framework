package live.lingting.kotlin.framework.ali.sts

import live.lingting.kotlin.framework.ali.AliClient
import live.lingting.kotlin.framework.ali.oss.AliOssActions
import live.lingting.kotlin.framework.ali.oss.AliOssBucket
import live.lingting.kotlin.framework.ali.oss.AliOssObject
import live.lingting.kotlin.framework.ali.properties.AliOssProperties
import live.lingting.kotlin.framework.ali.properties.AliStsProperties
import live.lingting.kotlin.framework.aws.AwsUtils
import live.lingting.kotlin.framework.aws.policy.Credential
import live.lingting.kotlin.framework.aws.policy.Statement
import live.lingting.kotlin.framework.aws.sts.AwsStsInterface
import live.lingting.kotlin.framework.http.util.HttpExtraUtils.convert
import live.lingting.kotlin.framework.json.JsonExtraUtils.jsonToObj
import live.lingting.kotlin.framework.time.DateTimePattern
import live.lingting.kotlin.framework.util.StringUtils
import kotlin.time.Duration

/**
 * @author lingting 2024-09-14 11:52
 */
open class AliStsClient(protected val properties: AliStsProperties) : AliClient<AliStsRequest>(properties),
    AwsStsInterface {

    override suspend fun credential(timeout: Duration, statements: Collection<Statement>): Credential {
        val request = AliStsCredentialRequest()
        request.timeout = timeout.inWholeSeconds
        request.statements = statements
        request.roleArn = properties.roleArn
        request.roleSessionName = properties.roleSessionName
        return credential(request)
    }

    suspend fun credential(request: AliStsCredentialRequest): Credential {
        return call(request).convert {
            val r = it.jsonToObj<AliStsCredentialResponse>()
            val ak = r.accessKeyId
            val sk = r.accessKeySecret
            val token = r.securityToken
            val expire = AwsUtils.parse(r.expire, DateTimePattern.FORMATTER_ISO_8601)
            Credential(ak, sk, token, expire)
        }
    }

    // region oss
    suspend fun ossBucket(region: String, bucket: String): AliOssBucket {
        val s = AliOssProperties()
        s.region = region
        s.bucket = bucket
        return ossBucket(s)
    }

    suspend fun ossBucket(region: String, bucket: String, actions: Collection<String>): AliOssBucket {
        val s = AliOssProperties()
        s.region = region
        s.bucket = bucket
        return ossBucket(s, actions)
    }

    suspend fun ossBucket(properties: AliOssProperties): AliOssBucket {
        return ossBucket(properties, AliOssActions.OSS_BUCKET_DEFAULT)
    }

    suspend fun ossBucket(properties: AliOssProperties, actions: Collection<String>): AliOssBucket {
        val bucket = if (StringUtils.hasText(properties.bucket)) properties.bucket else "*"
        val statement = Statement.allow()
        statement.addAction(actions)
        statement.addResource("acs:oss:*:*:$bucket")
        statement.addResource("acs:oss:*:*:$bucket/*")
        val credential = credential(statement)
        val copy = properties.copy()
        copy.useCredential(credential)
        return AliOssBucket(copy)
    }

    suspend fun ossObject(region: String, bucket: String, key: String): AliOssObject {
        val s = AliOssProperties()
        s.region = region
        s.bucket = bucket
        return ossObject(s, key)
    }

    suspend fun ossObject(region: String, bucket: String, key: String, actions: Collection<String>): AliOssObject {
        val s = AliOssProperties()
        s.region = region
        s.bucket = bucket
        return ossObject(s, key, actions)
    }

    suspend fun ossObject(properties: AliOssProperties, key: String): AliOssObject {
        return ossObject(properties, key, AliOssActions.OSS_OBJECT_DEFAULT)
    }

    suspend fun ossObject(properties: AliOssProperties, key: String, vararg actions: String) =
        ossObject(properties, key, actions.toList())

    suspend fun ossObject(properties: AliOssProperties, key: String, actions: Collection<String>): AliOssObject {
        val bucket = properties.bucket
        val statement = Statement.allow()
        statement.addAction(actions)
        statement.addResource("acs:oss:*:*:$bucket/$key")
        val credential = credential(statement)
        val copy = properties.copy()
        copy.useCredential(credential)
        return AliOssObject(copy, key)
    }
    // endregion

}
