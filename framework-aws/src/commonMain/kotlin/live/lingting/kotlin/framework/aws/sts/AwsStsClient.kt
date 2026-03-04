package live.lingting.kotlin.framework.aws.sts

import io.ktor.client.statement.bodyAsText
import live.lingting.kotlin.framework.aws.AwsClient
import live.lingting.kotlin.framework.aws.AwsUtils
import live.lingting.kotlin.framework.aws.policy.Credential
import live.lingting.kotlin.framework.aws.policy.Statement
import live.lingting.kotlin.framework.aws.properties.AwsS3Properties
import live.lingting.kotlin.framework.aws.properties.AwsStsProperties
import live.lingting.kotlin.framework.aws.s3.AwsS3Actions
import live.lingting.kotlin.framework.aws.s3.AwsS3Bucket
import live.lingting.kotlin.framework.aws.s3.AwsS3Object
import live.lingting.kotlin.framework.time.DateTimePattern
import live.lingting.kotlin.framework.util.StringUtils
import nl.adaptivity.xmlutil.serialization.XML
import kotlin.time.Duration

/**
 * @author lingting 2025/6/3 15:58
 */
class AwsStsClient(protected val properties: AwsStsProperties) : AwsClient<AwsStsRequest>(properties), AwsStsInterface {

    override fun service(): String = "sts"

    override suspend fun credential(timeout: Duration, statements: Collection<Statement>): Credential {
        val request = AwsStsCredentialRequest()
        request.roleArn = properties.roleArn
        request.roleSessionName = properties.roleSessionName
        request.sourceIdentity = properties.sourceIdentity

        request.timeout = timeout
        request.statements = statements
        return credential(request)
    }

    suspend fun credential(request: AwsStsCredentialRequest): Credential {
        val response = call(request)
        val string = response.bodyAsText()
        val convert = XML.decodeFromString<AwsStsCredentialResponse>(string)
        val ak = convert.accessKeyId
        val sk = convert.secretAccessKey
        val token = convert.sessionToken
        val expire = AwsUtils.parse(convert.expire, DateTimePattern.FORMATTER_ISO_8601)
        return Credential(ak, sk, token, expire)
    }

    // region s3
    suspend fun s3Bucket(region: String, bucket: String): AwsS3Bucket {
        val s = AwsS3Properties()
        s.region = region
        s.bucket = bucket
        return s3Bucket(s)
    }

    suspend fun s3Bucket(region: String, bucket: String, actions: Collection<String>): AwsS3Bucket {
        val s = AwsS3Properties()
        s.region = region
        s.bucket = bucket
        return s3Bucket(s, actions)
    }

    suspend fun s3Bucket(properties: AwsS3Properties): AwsS3Bucket {
        return s3Bucket(properties, AwsS3Actions.S3_BUCKET_DEFAULT)
    }

    suspend fun s3Bucket(properties: AwsS3Properties, actions: Collection<String>): AwsS3Bucket {
        val bucket = if (StringUtils.hasText(properties.bucket)) properties.bucket else "*"
        val statement = Statement.allow()
        statement.addAction(actions)
        statement.addResource("arn:aws:s3:*:*:$bucket")
        statement.addResource("arn:aws:s3:*:*:$bucket/*")
        val credential = credential(statement)
        val copy = properties.copy()
        copy.useCredential(credential)
        return AwsS3Bucket(copy)
    }

    suspend fun s3Object(region: String, bucket: String, key: String): AwsS3Object {
        val s = AwsS3Properties()
        s.region = region
        s.bucket = bucket
        return s3Object(s, key)
    }

    suspend fun s3Object(region: String, bucket: String, key: String, actions: Collection<String>): AwsS3Object {
        val s = AwsS3Properties()
        s.region = region
        s.bucket = bucket
        return s3Object(s, key, actions)
    }

    suspend fun s3Object(properties: AwsS3Properties, key: String): AwsS3Object {
        return s3Object(properties, key, AwsS3Actions.S3_OBJECT_DEFAULT)
    }

    suspend fun s3Object(properties: AwsS3Properties, key: String, vararg actions: String) =
        s3Object(properties, key, actions.toList())

    suspend fun s3Object(properties: AwsS3Properties, key: String, actions: Collection<String>): AwsS3Object {
        val bucket = properties.bucket
        val statement = Statement.allow()
        statement.addAction(actions)
        statement.addResource("arn:aws:s3:*:*:$bucket/$key")
        val credential = credential(statement)
        val copy = properties.copy()
        copy.useCredential(credential)
        return AwsS3Object(copy, key)
    }
    // endregion

}
