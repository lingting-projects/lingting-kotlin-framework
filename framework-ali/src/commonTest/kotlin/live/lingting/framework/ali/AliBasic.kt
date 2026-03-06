package live.lingting.framework.ali

import live.lingting.framework.ali.properties.AliOssProperties
import live.lingting.framework.ali.sts.AliSts
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.util.SystemUtils
import kotlin.test.assertNotNull


/**
 * @author lingting 2024-09-13 17:18
 */
internal object AliBasic {

    fun sts(): AliSts {
        val properties = _root_ide_package_.live.lingting.framework.ali.properties.AliStsProperties()
        properties.ak = assertNotNull(SystemUtils.getEnv("AK"))
        properties.sk = assertNotNull(SystemUtils.getEnv("SK"))
        properties.region = assertNotNull(SystemUtils.getEnv("REGION"))
        properties.roleArn =
            assertNotNull(SystemUtils.getEnv("ROLE_ARN"))
        properties.roleSessionName =
            assertNotNull(SystemUtils.getEnv("ROLE_SESSION_NAME"))
        return _root_ide_package_.live.lingting.framework.ali.sts.AliSts(properties)
    }

    fun ossStsProperties(): AliOssProperties {
        val properties = _root_ide_package_.live.lingting.framework.ali.properties.AliOssProperties()
        properties.region = assertNotNull(SystemUtils.getEnv("REGION"))
        properties.bucket = assertNotNull(SystemUtils.getEnv("BUCKET"))
        properties.acl = Acl.PRIVATE
        return properties
    }

    fun ossProperties(): AliOssProperties {
        val properties = _root_ide_package_.live.lingting.framework.ali.properties.AliOssProperties()
        properties.ak = assertNotNull(SystemUtils.getEnv("AK"))
        properties.sk = assertNotNull(SystemUtils.getEnv("SK"))
        properties.region = assertNotNull(SystemUtils.getEnv("REGION"))
        properties.bucket = assertNotNull(SystemUtils.getEnv("BUCKET"))
        properties.acl = Acl.PRIVATE
        return properties
    }

}
