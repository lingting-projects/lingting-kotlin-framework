package live.lingting.framework.huawei.iam

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import kotlinx.datetime.LocalDateTime
import live.lingting.framework.aws.policy.Credential
import live.lingting.framework.aws.policy.Statement
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.http.util.HttpExtraUtils.convert
import live.lingting.framework.http.util.HttpExtraUtils.use
import live.lingting.framework.http.util.HttpHeadersUtils.to
import live.lingting.framework.http.util.HttpUrlUtils.buildPath
import live.lingting.framework.http.util.HttpUtils.isOk
import live.lingting.framework.huawei.HuaweiUtils
import live.lingting.framework.huawei.HuaweiUtils.CREDENTIAL_EXPIRE
import live.lingting.framework.huawei.exception.HuaweiIamException
import live.lingting.framework.huawei.obs.HuaweiObsBucket
import live.lingting.framework.huawei.obs.HuaweiObsObject
import live.lingting.framework.huawei.properties.HuaweiIamProperties
import live.lingting.framework.huawei.properties.HuaweiObsProperties
import live.lingting.framework.json.JsonExtraUtils.jsonToObj
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.WaitValue
import kotlin.jvm.JvmField
import kotlin.jvm.JvmOverloads
import kotlin.time.Duration


/**
 * @author lingting 2024-09-12 21:27
 */
open class HuaweiIam(@JvmField val properties: HuaweiIamProperties) : ApiClient<HuaweiIamRequest>(properties.host) {

    /**
     * token 提前多久过期
     */
    var tokenEarlyExpire: Duration = HuaweiUtils.TOKEN_EARLY_EXPIRE

    /**
     * token 需要外部维护
     */
    var tokenValue: WaitValue<HuaweiIamToken> = WaitValue.Companion.of()
        protected set

    override fun hostUrlBuilder(): URLBuilder {
        return super.hostUrlBuilder().also {
            it.protocol = URLProtocol.HTTPS
        }
    }

    override suspend fun checkout(r: HuaweiIamRequest, request: HttpRequestBuilder, response: HttpResponse) {
        val httpStatus = response.status.value
        if (r is HuaweiIamTokenRequest && !response.isOk) {
            val msg = "[IAM] HUAWEI token获取异常! code: $httpStatus"
            log.error { msg }
            throw HuaweiIamException(msg)
        }

        if (httpStatus == 401) {
            log.debug { "HuaweiIam token expired!" }
            refreshToken(true)
            call(r)
            return
        }

        if (!response.isOk) {
            val string = response.bodyAsText()
            val msg = "[IAM] HUAWEI 请求异常! uri: ${request.url.buildPath()}; code: $httpStatus;"
            log.error { "$msg body: \n$string" }
            throw HuaweiIamException(msg)
        }
    }

    override suspend fun call(r: HuaweiIamRequest, builder: HttpRequestBuilder): HttpResponse {
        if (r.usingToken()) {
            refreshToken()
            val token = tokenValue.notNull()
            val headers = builder.headers
            headers["X-Auth-Token"] = token.value
        }
        return super.call(r, builder)
    }

    @JvmOverloads
    suspend fun refreshToken(force: Boolean = false) {
        tokenValue.withLockSuspend {
            val token = tokenValue.value
            // 强制刷新 或 token过期
            if (force || token == null || token.isExpired(tokenEarlyExpire)) {
                val v = token()
                tokenValue.value = v
            }
        }
    }

    suspend fun token(): HuaweiIamToken {
        val request = HuaweiIamTokenRequest()
        request.domain = properties.domain
        request.username = properties.username
        request.password = properties.password

        return call(request).use {
            val string = it.bodyAsText()
            val r = string.jsonToObj<HuaweiIamTokenResponse>()
            val token = requireNotNull(it.headers.to().first("X-Subject-Token")) {
                "[IAM] HUAWEI 获取Token失败! 返回头中不存在token"
            }
            val expire: LocalDateTime = HuaweiUtils.parse(r.expire, properties.zone)
            val issued: LocalDateTime = HuaweiUtils.parse(r.issued, properties.zone)
            HuaweiIamToken(token, expire, issued)
        }
    }

    suspend fun credential(statement: Statement): Credential {
        return credential(setOf(statement))
    }

    suspend fun credential(statement: Statement, vararg statements: Statement): Credential {
        val list: MutableList<Statement> = ArrayList(statements.size + 1)
        list.add(statement)
        list.addAll(statements)
        return credential(list)
    }

    suspend fun credential(statements: Collection<Statement>): Credential {
        return credential(CREDENTIAL_EXPIRE, statements)
    }

    suspend fun credential(timeout: Duration, statements: Collection<Statement>): Credential {
        val request = HuaweiIamCredentialRequest()
        request.timeout = timeout
        request.statements = statements
        return call(request).convert {
            val convert = it.jsonToObj<HuaweiIamCredentialResponse>()
            val ak = convert.access
            val sk = convert.secret
            val token = convert.securityToken
            val expire: LocalDateTime = HuaweiUtils.parse(convert.expire, properties.zone)
            Credential(ak, sk, token, expire)
        }
    }

    // region obs
    suspend fun obsBucket(region: String, bucket: String): HuaweiObsBucket {
        val s = HuaweiObsProperties()
        s.region = region
        s.bucket = bucket
        return obsBucket(s)
    }

    suspend fun obsBucket(region: String, bucket: String, actions: Collection<String>): HuaweiObsBucket {
        val s = HuaweiObsProperties()
        s.region = region
        s.bucket = bucket
        return obsBucket(s, actions)
    }

    suspend fun obsBucket(properties: HuaweiObsProperties): HuaweiObsBucket {
        return obsBucket(properties, live.lingting.framework.huawei.HuaweiActions.OBS_BUCKET_DEFAULT)
    }

    suspend fun obsBucket(properties: HuaweiObsProperties, actions: Collection<String>): HuaweiObsBucket {
        val bucket = if (StringUtils.hasText(properties.bucket)) properties.bucket else "*"
        val statement: Statement = Statement.allow()
        statement.addAction(actions)
        statement.addResource("obs:*:*:bucket:$bucket")
        statement.addResource("obs:*:*:object:$bucket/*")
        val credential = credential(statement)
        val copy = properties.copy()
        copy.useCredential(credential)
        return HuaweiObsBucket(copy)
    }

    suspend fun obsObject(region: String, bucket: String, key: String): HuaweiObsObject {
        val s = HuaweiObsProperties()
        s.region = region
        s.bucket = bucket
        return obsObject(s, key)
    }

    suspend fun obsObject(region: String, bucket: String, key: String, actions: Collection<String>): HuaweiObsObject {
        val s = HuaweiObsProperties()
        s.region = region
        s.bucket = bucket
        return obsObject(s, key, actions)
    }

    suspend fun obsObject(properties: HuaweiObsProperties, key: String): HuaweiObsObject {
        return obsObject(properties, key, live.lingting.framework.huawei.HuaweiActions.OBS_OBJECT_DEFAULT)
    }

    suspend fun obsObject(properties: HuaweiObsProperties, key: String, actions: Collection<String>): HuaweiObsObject {
        val bucket = properties.bucket
        val statement: Statement = Statement.allow()
        statement.addAction(actions)
        statement.addResource("obs:*:*:object:$bucket/$key")
        val credential = credential(statement)
        val copy = properties.copy()
        copy.useCredential(credential)
        return HuaweiObsObject(copy, key)
    }
    // endregion

}
