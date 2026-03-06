package live.lingting.framework.aws.signer

import io.ktor.http.HttpMethod
import kotlinx.datetime.LocalDateTime
import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.crypto.hmac.HmacSha256
import live.lingting.framework.crypto.util.DigestUtils.toSha256Hex
import live.lingting.framework.http.header.CollectionHttpHeaders
import live.lingting.framework.util.StringUtils.deleteLast
import live.lingting.framework.util.ValueUtils.forEachSorted
import live.lingting.framework.value.multi.StringMultiValue
import kotlin.jvm.JvmOverloads
import kotlin.time.Duration


/**
 * <a href="https://docs.aws.amazon.com/AmazonS3/latest/API/sig-v4-authenticating-requests.html">签名文档</a>
 * @author lingting 2025/5/21 19:40
 */
open class AwsV4Signer(
    val method: HttpMethod,
    path: String,
    headers: live.lingting.framework.http.header.HttpHeaders,
    val body: live.lingting.framework.http.body.Body<*>?,
    params: StringMultiValue,
    val region: String,
    ak: String,
    val sk: String,
    val service: String,
) : live.lingting.framework.aws.signer.AwsSigner<live.lingting.framework.aws.signer.AwsV4Signer, live.lingting.framework.aws.signer.AwsV4Signer.Signed>(
    ak
) {

    constructor(
        request: live.lingting.framework.http.api.ApiRequest,
        region: String,
        ak: String,
        sk: String,
        service: String
    ) : this(request.method(), request.path(), request.headers, request.body(), request.params, region, ak, sk, service)

    /**
     * 用于类似算法直接使用时设置
     */
    open val dateFormatter = AwsUtils.DATE_FORMATTER

    open val scopeDateFormatter = AwsUtils.SCOPE_DATE_FORMATTER

    open val algorithm = "AWS4-HMAC-SHA256"

    open val secretPrefix = "AWS4"

    open val scopeSuffix = "aws4_request"

    open val nameSignedHeaders = "SignedHeaders"

    open val nameAlgorithm = "Algorithm"

    open val headerPrefix = AwsUtils.HEADER_PREFIX

    /**
     * 除指定前缀开头的请求外, 其他参与签名的头
     */
    open val headerInclude =
        arrayOf("host", AwsUtils.HEADER_MD5, "range")

    open val headerDate by lazy { "$headerPrefix-date" }

    open val headerContentPayload by lazy { "$headerPrefix-content-sha256" }

    open val headerSecurityToken by lazy { "$headerPrefix-security-token" }

    open val headers = CollectionHttpHeaders().also { it.appendAll(headers) }

    open val params = StringMultiValue().also { it.appendAll(params) }

    open val path = path.let {
        if (path.startsWith("/")) path else "/$path"
    }

    override val bodyPayload by lazy {
        body.let {
            if (it == null || it.length() < 1) {
                AwsUtils.PAYLOAD_UNSIGNED
            } else {
                it.string().toSha256Hex()
            }
        }
    }

    open fun toParamName(key: String) = AwsUtils.toParamsKey(key)

    open fun addParam(key: String, value: String) {
        val s = if (!key.startsWith(headerPrefix)) "$headerPrefix-$key" else key
        val name = toParamName(s)
        params.append(name, value)
    }

    open fun token(): String? {
        val all = headers[headerSecurityToken]
        headers.remove(headerSecurityToken)
        return all.firstOrNull()
    }

    open fun date(time: LocalDateTime) =
        AwsUtils.format(time, dateFormatter)

    open fun canonicalUri() = path

    open fun canonicalQuery(): String {
        return live.lingting.framework.http.QueryBuilder(params).apply {
            emptyValueEqual = true
            sort = true
        }.build()
    }

    open fun headersForEach(block: (String, Collection<String>) -> Unit) {
        headers.forEachSorted { k, vs ->
            val n = k.lowercase()
            if (n.startsWith(headerPrefix) || headerInclude.contains(n)) {
                block(n, vs)
            }
        }
    }

    open fun canonicalHeaders(): String {
        val builder = StringBuilder()

        headersForEach { k, vs ->
            for (v in vs) {
                builder.append(k).append(":").append(v.trim()).append("\n")
            }
        }

        return builder.toString()
    }

    open fun signedHeaders(): String {
        val builder = StringBuilder()

        headersForEach { k, _ -> builder.append(k).append(";") }

        return deleteLast(builder).toString()
    }

    open fun canonicalRequest(): String {
        val canonicalUri = canonicalUri()
        val canonicalQuery = canonicalQuery()
        val canonicalHeaders = canonicalHeaders()
        val signedHeaders = signedHeaders()

        return canonicalRequest(canonicalUri, canonicalQuery, canonicalHeaders, signedHeaders)
    }

    @JvmOverloads
    open fun canonicalRequest(
        canonicalUri: String,
        canonicalQuery: String,
        canonicalHeaders: String,
        signedHeaders: String,
        bodyPayload: String = this.bodyPayload,
    ): String {
        return "$method\n$canonicalUri\n$canonicalQuery\n$canonicalHeaders\n$signedHeaders\n$bodyPayload"
    }

    open fun scopeDate(time: LocalDateTime) =
        AwsUtils.format(time, scopeDateFormatter)

    open fun scope(time: LocalDateTime) = scope(scopeDate(time))

    open fun scope(scopeDate: String): String {
        return "$scopeDate/$region/$service/$scopeSuffix"
    }

    /**
     * 签名源
     */
    open fun source(canonicalRequest: String, scopeDate: String, scope: String): String {
        val requestSha = canonicalRequest.toSha256Hex()
        return "$algorithm\n$scopeDate\n$scope\n$requestSha"
    }

    /**
     * 计算签名
     */
    open fun calculate(time: LocalDateTime): String {
        val request = canonicalRequest()
        val scopeDate = scopeDate(time)
        val scope = scope(scopeDate)
        return calculate(request, scopeDate, scope)
    }

    open fun calculate(canonicalRequest: String, scopeDate: String, scope: String): String {
        val source = source(canonicalRequest, scopeDate, scope)
        return calculate(scopeDate, source)
    }

    open fun calculate(scopeDate: String, source: String): String {
        val mac = HmacSha256("$secretPrefix$sk")
        val sourceKey1 = mac.calculate(scopeDate)
        val sourceKey2 = mac.useKey(sourceKey1).calculate(region)
        val sourceKey3 = mac.useKey(sourceKey2).calculate(service)
        val sourceKey4 = mac.useKey(sourceKey3).calculate(scopeSuffix)

        return mac.useKey(sourceKey4).calculateHex(source)
    }

    open fun authorization(scope: String, sign: String): String {
        val signedHeaders = signedHeaders()
        return authorization(scope, signedHeaders, sign)
    }

    open fun authorization(scope: String, signedHeaders: String, sign: String): String {
        return "$algorithm Credential=$ak/$scope, $nameSignedHeaders=$signedHeaders, Signature=$sign"
    }

    override fun signed(
        time: LocalDateTime,
        bodyPayload: String
    ): Signed {
        headers[headerContentPayload] = bodyPayload

        val date = date(time)
        headers[headerDate] = date

        val scopeDate = scopeDate(time)
        val scope = scope(scopeDate)

        val canonicalUri = canonicalUri()
        val canonicalQuery = canonicalQuery()
        val canonicalHeaders = canonicalHeaders()
        val signedHeaders = signedHeaders()

        val canonicalRequest =
            canonicalRequest(canonicalUri, canonicalQuery, canonicalHeaders, signedHeaders, bodyPayload)

        val source = source(canonicalRequest, date, scope)

        val calculate = calculate(scopeDate, source)

        val authorization = authorization(scope, signedHeaders, calculate)

        return Signed(
            this,
            headers,
            params,
            bodyPayload,
            canonicalUri,
            canonicalQuery,
            canonicalHeaders,
            signedHeaders,
            canonicalRequest,
            date,
            scopeDate,
            scope,
            source,
            calculate,
            authorization,
        )
    }

    override fun signed(
        time: LocalDateTime,
        duration: Duration,
        bodyPayload: String,
    ): Signed {
        val date = date(time)
        val token = token()
        if (!token.isNullOrEmpty()) {
            params[toParamName(headerSecurityToken)] = token
        }
        addParam(headerDate, date)
        addParam("Expires", duration.inWholeSeconds.toString())
        addParam(nameAlgorithm, algorithm)

        val scopeDate = scopeDate(time)
        val scope = scope(scopeDate)

        addParam("Credential", "$ak/$scope")
        val signedHeaders = signedHeaders()

        addParam(nameSignedHeaders, signedHeaders)

        val canonicalUri = canonicalUri()
        val canonicalQuery = canonicalQuery()
        val canonicalHeaders = canonicalHeaders()

        val canonicalRequest =
            canonicalRequest(canonicalUri, canonicalQuery, canonicalHeaders, signedHeaders, bodyPayload)

        val source = source(canonicalRequest, date, scope)

        val calculate = calculate(scopeDate, source)

        addParam("Signature", calculate)

        return Signed(
            this,
            headers,
            params,
            bodyPayload,
            canonicalUri,
            canonicalQuery,
            canonicalHeaders,
            signedHeaders,
            canonicalRequest,
            date,
            scopeDate,
            scope,
            source,
            calculate,
            "",
        )
    }

    open class Signed(
        signer: live.lingting.framework.aws.signer.AwsV4Signer,
        headers: live.lingting.framework.http.header.HttpHeaders,
        params: StringMultiValue?,
        bodyPayload: String,
        open val canonicalUri: String,
        open val canonicalQuery: String,
        open val canonicalHeaders: String,
        open val signedHeaders: String,
        open val canonicalRequest: String,
        open val date: String,
        open val scopeDate: String,
        open val scope: String,
        source: String,
        sign: String,
        authorization: String,
    ) : live.lingting.framework.aws.signer.AwsSigner.Signed<live.lingting.framework.aws.signer.AwsV4Signer, Signed>(
        signer,
        headers,
        params,
        bodyPayload,
        source,
        sign,
        authorization
    )

}
