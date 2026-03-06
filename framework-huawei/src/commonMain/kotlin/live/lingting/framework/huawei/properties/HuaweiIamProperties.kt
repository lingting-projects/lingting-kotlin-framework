package live.lingting.framework.huawei.properties

import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.JsonElement
import live.lingting.framework.time.DateTimePattern

/**
 * @author lingting 2024-09-12 21:31
 */
class HuaweiIamProperties {

    var host: String = "iam.myhuaweicloud.com"

    var domain: Map<String, JsonElement> = emptyMap()

    var username: String = ""

    var password: String = ""

    var zone: TimeZone = DateTimePattern.UTC8_ZONE

}
