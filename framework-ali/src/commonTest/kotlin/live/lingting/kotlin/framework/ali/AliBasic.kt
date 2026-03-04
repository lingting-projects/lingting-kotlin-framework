package live.lingting.kotlin.framework.ali

import live.lingting.kotlin.framework.ali.properties.AliOssProperties
import live.lingting.kotlin.framework.ali.properties.AliStsProperties
import live.lingting.kotlin.framework.ali.sts.AliStsClient
import live.lingting.kotlin.framework.aws.policy.Acl
import live.lingting.kotlin.framework.util.SystemUtils
import kotlin.test.assertNotNull


/**
 * @author lingting 2024-09-13 17:18
 */
internal object AliBasic {

    fun sts(): AliStsClient {
        val properties = AliStsProperties()
        properties.ak = assertNotNull(SystemUtils.getEnv("AK"))
        properties.sk = assertNotNull(SystemUtils.getEnv("SK"))
        properties.region = assertNotNull(SystemUtils.getEnv("REGION"))
        properties.roleArn = assertNotNull(SystemUtils.getEnv("ROLE_ARN"))
        properties.roleSessionName = assertNotNull(SystemUtils.getEnv("ROLE_SESSION_NAME"))
        return AliStsClient(properties)
    }

    fun ossStsProperties(): AliOssProperties {
        val properties = AliOssProperties()
        properties.region = assertNotNull(SystemUtils.getEnv("REGION"))
        properties.bucket = assertNotNull(SystemUtils.getEnv("BUCKET"))
        properties.acl = Acl.PRIVATE
        return properties
    }

    fun ossProperties(): AliOssProperties {
        val properties = AliOssProperties()
        properties.ak = assertNotNull(SystemUtils.getEnv("AK"))
        properties.sk = assertNotNull(SystemUtils.getEnv("SK"))
        properties.region = assertNotNull(SystemUtils.getEnv("REGION"))
        properties.bucket = assertNotNull(SystemUtils.getEnv("BUCKET"))
        properties.acl = Acl.PRIVATE
        return properties
    }

}
