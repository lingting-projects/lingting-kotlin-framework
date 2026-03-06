package live.lingting.framework.aws

import kotlin.test.assertNotNull

object AwsBasic {

    fun sts(): live.lingting.framework.aws.sts.AwsSts {
        val properties = _root_ide_package_.live.lingting.framework.aws.properties.AwsStsProperties()
        properties.ak = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("AK"))
        properties.sk = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("SK"))
        properties.region = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("REGION"))
        properties.roleArn =
            assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("ROLE_ARN"))
        properties.roleSessionName =
            assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("ROLE_SESSION_NAME"))
        properties.sourceIdentity =
            _root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("SOURCE_IDENTITY") ?: ""
        return _root_ide_package_.live.lingting.framework.aws.sts.AwsSts(properties)
    }

    fun s3StsProperties(): live.lingting.framework.aws.properties.AwsS3Properties {
        val properties = _root_ide_package_.live.lingting.framework.aws.properties.AwsS3Properties()
        properties.region = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("REGION"))
        properties.bucket = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("BUCKET"))
        properties.acl = _root_ide_package_.live.lingting.framework.aws.policy.Acl.PUBLIC_READ
        return properties
    }

    fun s3Properties(): live.lingting.framework.aws.properties.AwsS3Properties {
        val properties = _root_ide_package_.live.lingting.framework.aws.properties.AwsS3Properties()
        properties.ak = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("AK"))
        properties.sk = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("SK"))
        properties.region = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("REGION"))
        properties.bucket = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("BUCKET"))
        properties.acl = _root_ide_package_.live.lingting.framework.aws.policy.Acl.PUBLIC_READ
        return properties
    }

}
