package live.lingting.framework.http.util

import io.ktor.client.statement.HttpResponse
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/12 14:50
 */
object HttpUtils {

    @JvmStatic
    inline val HttpResponse.isOk get() = status.value in 200..299

}
