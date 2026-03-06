package live.lingting.framework.aws

import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.properties.AwsS3Properties
import live.lingting.framework.aws.properties.AwsStsProperties
import live.lingting.framework.aws.sts.AwsSts
import live.lingting.framework.util.SystemUtils
import kotlin.test.assertNotNull

object AwsBasic {

    fun sts(): AwsSts {
        val properties = AwsStsProperties()
        properties.ak = assertNotNull(SystemUtils.getEnv("AK"))
        properties.sk = assertNotNull(SystemUtils.getEnv("SK"))
        properties.region = assertNotNull(SystemUtils.getEnv("REGION"))
        properties.roleArn =
            assertNotNull(SystemUtils.getEnv("ROLE_ARN"))
        properties.roleSessionName =
            assertNotNull(SystemUtils.getEnv("ROLE_SESSION_NAME"))
        properties.sourceIdentity =
            SystemUtils.getEnv("SOURCE_IDENTITY") ?: ""
        return AwsSts(properties)
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
