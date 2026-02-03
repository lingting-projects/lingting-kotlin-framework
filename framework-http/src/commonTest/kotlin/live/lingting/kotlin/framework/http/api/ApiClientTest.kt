package live.lingting.kotlin.framework.http.api

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.request.header
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.toMap
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import live.lingting.kotlin.framework.http.body.MemoryBody
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * @author lingting 2026/2/3 17:54
 */
class ApiClientTest {

    companion object {

        const val authValue = "secure-key"

        const val noAuthKey = "noAuth"

    }

    @Test
    fun `test api client logic`() = runTest {

        // 2. 启动随机端口 Server
        val server = embeddedServer(CIO, host = "127.0.0.1", port = 0) {
            routing {
                // 鉴权逻辑：手动判断 Header
                val checkAuth: suspend (io.ktor.server.application.ApplicationCall) -> Boolean = { call ->
                    if (call.request.header("Authorization") != authValue) {
                        val errorJson = Json.encodeToString(R(403, "Forbidden"))
                        call.respondText(errorJson, io.ktor.http.ContentType.Application.Json, HttpStatusCode.Forbidden)
                        false
                    } else true
                }

                get("/get") {
                    if (checkAuth(call)) {
                        call.respondText(Json.encodeToString(R(200, call.request.queryParameters.toMap())))
                    }
                }

                post("/post") {
                    if (checkAuth(call)) {
                        val body = call.receiveText()
                        call.respondText(Json.encodeToString(R(200, body)))
                    }
                }
            }
        }.start(wait = false)

        try {
            val port = server.engine.resolvedConnectors().first().port
            val host = "http://127.0.0.1:$port"
            val apiClient = TestApiClient(host)

            // --- 场景 1: 正确的 GET 请求 ---
            val getReq = ApiSimpleRequest(HttpMethod.Get, "/get").apply {
                params["name"] = "lingting"
            }
            val getResp = apiClient.request(getReq)
            assertEquals(200, getResp.status.value)
            val getResultString = getResp.bodyAsText()
            println("Correct GET: $getResultString")

            val getResult = Json.decodeFromString<R<Map<String, Array<String>>>>(getResultString)
            val getResultData = getResult.data
            assertNotNull(getResultData)
            assertFalse(getResultData.isEmpty())
            assertEquals("lingting", getResultData["name"]?.first())

            // --- 场景 2: 正确的 POST 请求 ---
            val postContent = "hello-world"
            val postReq = ApiSimpleRequest(HttpMethod.Post, "/post", MemoryBody(postContent))

            val postResp = apiClient.request(postReq)
            val postResultString = postResp.bodyAsText()
            println("Correct POST: $postResultString")
            assertEquals(200, postResp.status.value)

            val postResult = Json.decodeFromString<R<String>>(postResultString)
            assertEquals(postContent, postResult.data)

            // --- 场景 3: 异常请求 (鉴权失败) ---
            val failReq = ApiSimpleRequest(HttpMethod.Get, "/get").apply {
                params[noAuthKey] = ""
            }
            val exception = assertFailsWith<IllegalStateException> {
                apiClient.request(failReq)
            }
            // 进一步校验异常信息是否包含状态码（对应你 TestApiClient 中的抛出逻辑）
            assertTrue(exception.message!!.contains("status=403 Forbidden"))
            println("Verified Exception: ${exception.message}")

        } finally {
            server.stop(500, 500)
        }
    }

    class TestApiClient(host: String) : ApiClient<ApiSimpleRequest>(host) {

        // 在这里实现校验逻辑
        override suspend fun checkout(r: ApiSimpleRequest, response: HttpResponse) {
            if (response.status != HttpStatusCode.OK) {
                throw IllegalStateException("Check failed: status=${response.status}")
            }

            val body = response.bodyAsText()
            val result = Json.decodeFromString<R<JsonElement>>(body)

            if (result.code != 200) {
                throw IllegalStateException("Check failed: code=${result.code}")
            }
        }

        override fun onHeadersAfter(r: ApiSimpleRequest, headers: HeadersBuilder) {
            if (!r.params.contains(noAuthKey)) {
                headers["Authorization"] = authValue
            }
        }

        suspend fun request(r: ApiSimpleRequest): HttpResponse {
            return call(r)
        }

    }

    @Serializable
    data class R<T>(val code: Int, val data: T? = null)

}
