package live.lingting.kotlin.framework.aws

import live.lingting.kotlin.framework.aws.policy.Acl
import live.lingting.kotlin.framework.aws.properties.AwsS3Properties
import live.lingting.kotlin.framework.aws.properties.AwsStsProperties
import live.lingting.kotlin.framework.aws.sts.AwsStsClient
import live.lingting.kotlin.framework.util.SystemUtils
import kotlin.test.assertNotNull

object AwsBasic {

    fun sts(): AwsStsClient {
        val properties = AwsStsProperties()
        properties.ak = assertNotNull(SystemUtils.getEnv("AK"))
        properties.sk = assertNotNull(SystemUtils.getEnv("SK"))
        properties.region = assertNotNull(SystemUtils.getEnv("REGION"))
        properties.roleArn = assertNotNull(SystemUtils.getEnv("ROLE_ARN"))
        properties.roleSessionName = assertNotNull(SystemUtils.getEnv("ROLE_SESSION_NAME"))
        properties.sourceIdentity = SystemUtils.getEnv("SOURCE_IDENTITY") ?: ""
        return AwsStsClient(properties)
    }

    fun s3StsProperties(): AwsS3Properties {
        val properties = AwsS3Properties()
        properties.region = assertNotNull(SystemUtils.getEnv("REGION"))
        properties.bucket = assertNotNull(SystemUtils.getEnv("BUCKET"))
        properties.acl = Acl.PUBLIC_READ
        return properties
    }

    fun s3Properties(): AwsS3Properties {
        val properties = AwsS3Properties()
        properties.ak = assertNotNull(SystemUtils.getEnv("AK"))
        properties.sk = assertNotNull(SystemUtils.getEnv("SK"))
        properties.region = assertNotNull(SystemUtils.getEnv("REGION"))
        properties.bucket = assertNotNull(SystemUtils.getEnv("BUCKET"))
        properties.acl = Acl.PUBLIC_READ
        return properties
    }

}
