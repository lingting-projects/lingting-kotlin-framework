package live.lingting.kotlin.framework.ali

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * @author lingting 2024-09-14 13:51
 */
@Serializable
abstract class AliResponse {

    @SerialName("RequestId")
    var requestId: String = ""

}
