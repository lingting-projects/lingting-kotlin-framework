package live.lingting.framework.ali.signer


import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import live.lingting.framework.ali.AliUtils
import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.http.util.HttpUrlUtils.buildPath
import live.lingting.framework.http.util.HttpUrlUtils.headerHost
import live.lingting.framework.http.util.ParametersUtils.appendAll
import live.lingting.framework.util.DurationUtils.days
import live.lingting.framework.value.multi.StringMultiValue
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author lingting 2024-09-19 20:37
 */
class AliV4SignerTest {

    @Test
    fun test() {
        testHeader()
        testParams()
        testParamsToken()
    }

    fun testParamsToken() {
        val params = _root_ide_package_.live.lingting.framework.value.multi.StringMultiValue()

        val builder = URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            host = "examplebucket.oss.cn-shanghai.aliyuncs.com"
            appendPathSegments("test.txt")
        }

        val headers = live.lingting.framework.http.header.HttpHeaders.empty()
        headers.put("Host", builder.headerHost())
        headers.put(
            live.lingting.framework.aws.AwsUtils.HEADER_TOKEN,
            "IQoJb3JpZ2luX2VjEMv//////////wEaCXVzLWVhc3QtMSJGMEQCIBSUbVdj9YGs2g0HkHsOHFdkwOozjARSKHL987NhhOC8AiBPepRU1obMvIbGU0T+WphFPgK/qpxaf5Snvm5M57XFkCqlAgjz//////////8BEAAaDDQ3MjM4NTU0NDY2MCIM83pULBe5/+Nm1GZBKvkBVslSaJVgwSef7SsoZCJlfJ56weYl3QCwEGr2F4BmCZZyFpmWEYzWnhNK1AnHMj5nkfKlKBx30XAT5PZGVrmq4Vkn9ewlXQy1Iu3QJRi9Tdod8Ef9/yajTaUGh76+F5u5a4O115jwultOQiKomVwO318CO4l8lv/3HhMOkpdanMXn+4PY8lvM8RgnzSu90jOUpGXEOAo/6G8OqlMim3+ZmaQmasn4VYRvESEd7O72QGZ3+vDnDVnss0lSYjlv8PP7IujnvhZRnj0WoeOyMe1lL0wTG/a9usH5hE52w/YUJccOn0OaZuyROuVsRV4Q70sbWQhUvYUt+0tUMKzm8vsFOp4BaNZFqobbjtb36Y92v+x5kY6i0s8QE886jJtUWMP5ldMziClGx3p0mN5dzsYlM3GyiJ/O1mWkPQDwg3mtSpOA9oeeuAMPTA7qMqy9RNuTKBDSx9EW27wvPzBum3SJhEfxv48euadKgrIX3Z79ruQFSQOc9LUrDjR+4SoWAJqK+GX8Q3vPSjsLxhqhEMWd6U4TXcM7ku3gxMbzqfT8NDg="
        )

        headers.keys().forEach { name ->
            if (name == live.lingting.framework.aws.AwsUtils.HEADER_ACL) {
                headers.replace(name, _root_ide_package_.live.lingting.framework.ali.AliUtils.HEADER_ACL)
            } else if (name.startsWith(live.lingting.framework.aws.AwsUtils.HEADER_PREFIX)) {
                val newName = name.replace(
                    live.lingting.framework.aws.AwsUtils.HEADER_PREFIX,
                    _root_ide_package_.live.lingting.framework.ali.AliUtils.HEADER_PREFIX
                )
                headers.replace(name, newName)
            }
        }

        val signer = _root_ide_package_.live.lingting.framework.ali.signer.AliV4Signer(
            HttpMethod.Get,
            builder.buildPath(),
            headers,
            null,
            params,
            "cn-shanghai",
            "AKIAIOSFODNN7EXAMPLE",
            "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
            "oss"
        )

        val dateTime = live.lingting.framework.aws.AwsUtils.parse("20200524T000000Z", signer.dateFormatter)

        val signed = signer.signed(dateTime, 1.days)

        assertEquals(
            "GET\n" +
                    "/test.txt\n" +
                    "x-oss-credential=AKIAIOSFODNN7EXAMPLE%2F20200524%2Fcn-shanghai%2Foss%2Faliyun_v4_request&x-oss-date=20200524T000000Z&x-oss-expires=86400&x-oss-security-token=IQoJb3JpZ2luX2VjEMv%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXVzLWVhc3QtMSJGMEQCIBSUbVdj9YGs2g0HkHsOHFdkwOozjARSKHL987NhhOC8AiBPepRU1obMvIbGU0T%2BWphFPgK%2Fqpxaf5Snvm5M57XFkCqlAgjz%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8BEAAaDDQ3MjM4NTU0NDY2MCIM83pULBe5%2F%2BNm1GZBKvkBVslSaJVgwSef7SsoZCJlfJ56weYl3QCwEGr2F4BmCZZyFpmWEYzWnhNK1AnHMj5nkfKlKBx30XAT5PZGVrmq4Vkn9ewlXQy1Iu3QJRi9Tdod8Ef9%2FyajTaUGh76%2BF5u5a4O115jwultOQiKomVwO318CO4l8lv%2F3HhMOkpdanMXn%2B4PY8lvM8RgnzSu90jOUpGXEOAo%2F6G8OqlMim3%2BZmaQmasn4VYRvESEd7O72QGZ3%2BvDnDVnss0lSYjlv8PP7IujnvhZRnj0WoeOyMe1lL0wTG%2Fa9usH5hE52w%2FYUJccOn0OaZuyROuVsRV4Q70sbWQhUvYUt%2B0tUMKzm8vsFOp4BaNZFqobbjtb36Y92v%2Bx5kY6i0s8QE886jJtUWMP5ldMziClGx3p0mN5dzsYlM3GyiJ%2FO1mWkPQDwg3mtSpOA9oeeuAMPTA7qMqy9RNuTKBDSx9EW27wvPzBum3SJhEfxv48euadKgrIX3Z79ruQFSQOc9LUrDjR%2B4SoWAJqK%2BGX8Q3vPSjsLxhqhEMWd6U4TXcM7ku3gxMbzqfT8NDg%3D&x-oss-signature-version=OSS4-HMAC-SHA256\n" +
                    "\n" +
                    "\n" +
                    "UNSIGNED-PAYLOAD", signed.canonicalRequest
        )

        assertEquals(
            "OSS4-HMAC-SHA256\n" +
                    "20200524T000000Z\n" +
                    "20200524/cn-shanghai/oss/aliyun_v4_request\n" +
                    "7682632efaec2ea9d7c014526036bb498b9b517b250c6e9d487509c4d42bd3ec", signed.source
        )

        assertEquals("bff8e53019525c2213f9e4e006f8512004f90be16ea1807bb837ccfa253ee16d", signed.sign)

        signed.params?.run { builder.parameters.appendAll(this) }

        assertEquals(
            "https://examplebucket.oss.cn-shanghai.aliyuncs.com/test.txt?x-oss-security-token=IQoJb3JpZ2luX2VjEMv%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXVzLWVhc3QtMSJGMEQCIBSUbVdj9YGs2g0HkHsOHFdkwOozjARSKHL987NhhOC8AiBPepRU1obMvIbGU0T%2BWphFPgK%2Fqpxaf5Snvm5M57XFkCqlAgjz%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8BEAAaDDQ3MjM4NTU0NDY2MCIM83pULBe5%2F%2BNm1GZBKvkBVslSaJVgwSef7SsoZCJlfJ56weYl3QCwEGr2F4BmCZZyFpmWEYzWnhNK1AnHMj5nkfKlKBx30XAT5PZGVrmq4Vkn9ewlXQy1Iu3QJRi9Tdod8Ef9%2FyajTaUGh76%2BF5u5a4O115jwultOQiKomVwO318CO4l8lv%2F3HhMOkpdanMXn%2B4PY8lvM8RgnzSu90jOUpGXEOAo%2F6G8OqlMim3%2BZmaQmasn4VYRvESEd7O72QGZ3%2BvDnDVnss0lSYjlv8PP7IujnvhZRnj0WoeOyMe1lL0wTG%2Fa9usH5hE52w%2FYUJccOn0OaZuyROuVsRV4Q70sbWQhUvYUt%2B0tUMKzm8vsFOp4BaNZFqobbjtb36Y92v%2Bx5kY6i0s8QE886jJtUWMP5ldMziClGx3p0mN5dzsYlM3GyiJ%2FO1mWkPQDwg3mtSpOA9oeeuAMPTA7qMqy9RNuTKBDSx9EW27wvPzBum3SJhEfxv48euadKgrIX3Z79ruQFSQOc9LUrDjR%2B4SoWAJqK%2BGX8Q3vPSjsLxhqhEMWd6U4TXcM7ku3gxMbzqfT8NDg%3D&x-oss-date=20200524T000000Z&x-oss-expires=86400&x-oss-signature-version=OSS4-HMAC-SHA256&x-oss-credential=AKIAIOSFODNN7EXAMPLE%2F20200524%2Fcn-shanghai%2Foss%2Faliyun_v4_request&x-oss-signature=bff8e53019525c2213f9e4e006f8512004f90be16ea1807bb837ccfa253ee16d",
            builder.buildString()
        )
    }

    fun testParams() {
        val params = _root_ide_package_.live.lingting.framework.value.multi.StringMultiValue()

        val builder = URLBuilder().apply {
            protocol = URLProtocol.HTTPS
            host = "examplebucket.oss.aliyuncs.com"
            appendPathSegments("test.txt")
        }

        val headers = live.lingting.framework.http.header.HttpHeaders.empty()
        headers.put("Host", builder.headerHost())

        val signer = _root_ide_package_.live.lingting.framework.ali.signer.AliV4Signer(
            HttpMethod.Get,
            builder.buildPath(),
            headers,
            null,
            params,
            "cn-shanghai",
            "AKIAIOSFODNN7EXAMPLE",
            "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
            "oss"
        )

        val dateTime = live.lingting.framework.aws.AwsUtils.parse("20130524T000000Z", signer.dateFormatter)

        val signed = signer.signed(dateTime, 1.days)

        assertEquals(
            "GET\n" +
                    "/test.txt\n" +
                    "x-oss-credential=AKIAIOSFODNN7EXAMPLE%2F20130524%2Fcn-shanghai%2Foss%2Faliyun_v4_request&x-oss-date=20130524T000000Z&x-oss-expires=86400&x-oss-signature-version=OSS4-HMAC-SHA256\n\n" +
                    "\n" +
                    "UNSIGNED-PAYLOAD", signed.canonicalRequest
        )

        assertEquals(
            "OSS4-HMAC-SHA256\n" +
                    "20130524T000000Z\n" +
                    "20130524/cn-shanghai/oss/aliyun_v4_request\n" +
                    "be61c2b62629513bd4f8fd5ac255f8ee2f4de2c27ff027f99921d0a89825e8d4", signed.source
        )

        assertEquals("8f5e56021704836d6bf4ae9b0ce47b74c3c31e231a54d3100282c40e388a9019", signed.sign)

        signed.params?.run { builder.parameters.appendAll(this) }

        assertEquals(
            "https://examplebucket.oss.aliyuncs.com/test.txt?x-oss-date=20130524T000000Z&x-oss-expires=86400&x-oss-signature-version=OSS4-HMAC-SHA256&x-oss-credential=AKIAIOSFODNN7EXAMPLE%2F20130524%2Fcn-shanghai%2Foss%2Faliyun_v4_request&x-oss-signature=8f5e56021704836d6bf4ae9b0ce47b74c3c31e231a54d3100282c40e388a9019",
            builder.buildString()
        )
    }

    fun testHeader() {
        val params = _root_ide_package_.live.lingting.framework.value.multi.StringMultiValue()

        val headers = live.lingting.framework.http.header.HttpHeaders.empty()
        headers.put("Host", "examplebucket.oss.aliyuncs.com")
        headers.put("Range", "bytes=0-9")

        val signer = _root_ide_package_.live.lingting.framework.ali.signer.AliV4Signer(
            HttpMethod.Get,
            "/test.txt",
            headers,
            null,
            params,
            "cn-shanghai",
            "AKIAIOSFODNN7EXAMPLE",
            "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
            "oss"
        )

        val bodyPayload = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        val dateTime = live.lingting.framework.aws.AwsUtils.parse("20130524T000000Z", signer.dateFormatter)
        val signed = signer.signed(dateTime, bodyPayload)

        assertEquals(bodyPayload, signed.bodyPayload)
        assertEquals("/test.txt", signed.canonicalUri)
        assertEquals("", signed.canonicalQuery)
        assertEquals(
            "x-oss-content-sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\n" +
                    "x-oss-date:20130524T000000Z\n", signed.canonicalHeaders
        )
        assertEquals("", signed.signedHeaders)
        assertEquals(
            """
                    GET
                    /test.txt

                    x-oss-content-sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
                    x-oss-date:20130524T000000Z


                    e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
                    """.trimIndent(), signed.canonicalRequest
        )
        assertEquals("20130524T000000Z", signed.date)
        assertEquals("20130524", signed.scopeDate)
        assertEquals("20130524/cn-shanghai/oss/aliyun_v4_request", signed.scope)
        assertEquals(
            """
                OSS4-HMAC-SHA256
                20130524T000000Z
                20130524/cn-shanghai/oss/aliyun_v4_request
                57e01b52313cd7a96e61f5200d4e0a04efc7e6748587e8624ead3222850e6965
                    """.trimIndent(), signed.source
        )

        assertEquals("887f90f12b1acedfef133fb12cb8ef8987d3e6b89dd1cb4756fe0a4fdb0e70d6", signed.sign)
        assertEquals(
            "OSS4-HMAC-SHA256 Credential=AKIAIOSFODNN7EXAMPLE/20130524/cn-shanghai/oss/aliyun_v4_request, Signature=887f90f12b1acedfef133fb12cb8ef8987d3e6b89dd1cb4756fe0a4fdb0e70d6",
            signed.authorization
        )
    }

}
