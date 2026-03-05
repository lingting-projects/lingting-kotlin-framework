package live.lingting.kotlin.framework.http

import io.ktor.client.request.get
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author lingting 2026/1/31 17:18
 */
class HttpClientTest {

    @Test
    fun test() = runTest {
        val client = HttpClients.default()
        val response = client.get("https://www.baidu.com")
        assertEquals(200, response.status.value)
    }

}
