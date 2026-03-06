package live.lingting.framework.huawei

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.huawei.iam.HuaweiIam
import live.lingting.framework.huawei.properties.HuaweiIamProperties
import live.lingting.framework.huawei.properties.HuaweiObsProperties
import live.lingting.framework.util.SystemUtils
import kotlin.test.assertNotNull


/**
 * @author lingting 2024-09-13 17:18
 */
internal object HuaweiBasic {
    fun iam(): HuaweiIam {
        val properties = HuaweiIamProperties()
        val name = SystemUtils.getEnv("IAM_DOMAIN_NAME")
        assertNotNull(name)
        properties.domain = buildJsonObject {
            put("name", name)
        }
        properties.username = assertNotNull(SystemUtils.getEnv("IAM_USERNAME"))
        properties.password = assertNotNull(SystemUtils.getEnv("IAM_PASSWORD"))
        val iam = HuaweiIam(properties)
        return iam
    }

    fun obsProperties(): HuaweiObsProperties {
        val properties = HuaweiObsProperties()
        properties.region = assertNotNull(SystemUtils.getEnv("REGION"))
        properties.bucket = assertNotNull(SystemUtils.getEnv("BUCKET"))
        properties.acl = Acl.PUBLIC_READ
        return properties
    }
}
