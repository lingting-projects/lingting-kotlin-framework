package live.lingting.framework.ali

import kotlin.test.assertNotNull


/**
 * @author lingting 2024-09-13 17:18
 */
internal object AliBasic {

    fun sts(): live.lingting.framework.ali.sts.AliSts {
        val properties = _root_ide_package_.live.lingting.framework.ali.properties.AliStsProperties()
        properties.ak = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("AK"))
        properties.sk = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("SK"))
        properties.region = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("REGION"))
        properties.roleArn =
            assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("ROLE_ARN"))
        properties.roleSessionName =
            assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("ROLE_SESSION_NAME"))
        return _root_ide_package_.live.lingting.framework.ali.sts.AliSts(properties)
    }

    fun ossStsProperties(): live.lingting.framework.ali.properties.AliOssProperties {
        val properties = _root_ide_package_.live.lingting.framework.ali.properties.AliOssProperties()
        properties.region = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("REGION"))
        properties.bucket = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("BUCKET"))
        properties.acl = live.lingting.framework.aws.policy.Acl.PRIVATE
        return properties
    }

    fun ossProperties(): live.lingting.framework.ali.properties.AliOssProperties {
        val properties = _root_ide_package_.live.lingting.framework.ali.properties.AliOssProperties()
        properties.ak = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("AK"))
        properties.sk = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("SK"))
        properties.region = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("REGION"))
        properties.bucket = assertNotNull(_root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("BUCKET"))
        properties.acl = live.lingting.framework.aws.policy.Acl.PRIVATE
        return properties
    }

}
