package live.lingting.framework.aws.s3

import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.http
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.statement.bodyAsText
import io.ktor.util.appendAll
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.CoroutineScope
import live.lingting.framework.async.async
import live.lingting.framework.crypto.util.DigestUtils.toMd5Hex
import live.lingting.framework.http.util.HttpExtraUtils.use
import live.lingting.framework.http.util.HttpUtils.isOk
import live.lingting.framework.util.DataSizeUtils.bytes
import live.lingting.framework.util.DurationUtils.millis
import live.lingting.framework.util.LoggerUtils.logger
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


/**
 * @author lingting 2025/8/27 11:35
 */
abstract class S3BasicTest {

    private val log = logger()

    private val snowflake = _root_ide_package_.live.lingting.framework.snowflake.Snowflake(0, 0)

    protected open val useProxy: Boolean = false

    abstract suspend fun buildObj(key: String): live.lingting.framework.aws.s3.interfaces.AwsS3ObjectDelegation

    abstract suspend fun buildBucket(): live.lingting.framework.aws.s3.interfaces.AwsS3BucketDelegation

    abstract fun properties(): live.lingting.framework.aws.properties.S3Properties

    protected val properties = properties()

    protected var client: io.ktor.client.HttpClient = live.lingting.framework.http.api.ApiClient.defaultClient

    private fun before() {
        if (useProxy) {
            client = live.lingting.framework.http.HttpClients.build {
                disableSsl()
                proxy(ProxyBuilder.http("http://127.0.0.1:9999"))
            }
        }
    }

    suspend fun CoroutineScope.run() {
        before()
        live.lingting.framework.http.api.ApiClient.defaultClient = client
        _root_ide_package_.live.lingting.framework.util.CoroutineUtils.switchScope(this)
        doTest()
        val domain = _root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("DOMAIN")
        if (!domain.isNullOrBlank()) {
            properties.domain = domain
            doTest()
        }
        val domain1 = _root_ide_package_.live.lingting.framework.util.SystemUtils.getEnv("DOMAIN1")
        if (!domain1.isNullOrBlank()) {
            properties.domain = domain1
            doTest()
        }
    }

    protected open suspend fun doTest() {
        val async = _root_ide_package_.live.lingting.framework.async.async(1)
        val atomic = AtomicLong(0L)

        async.submit {
            try {
                put()
            } catch (t: Throwable) {
                atomic.incrementAndFetch()
                log.error(t) { "[put] 测试异常! " }
            }
        }
        async.submit {
            try {
                multipart()
            } catch (t: Throwable) {
                atomic.incrementAndFetch()
                log.error(t) { "[multipart] 测试异常! " }
            }
        }
        async.submit {
            try {
                pre()
            } catch (t: Throwable) {
                atomic.incrementAndFetch()
                log.error(t) { "[pre] 测试异常! " }
            }
        }
        async.submit {
            try {
                listAndMeta()
            } catch (t: Throwable) {
                atomic.incrementAndFetch()
                log.error(t) { "[listAndMeta] 测试异常! " }
            }
        }

        async.await()
        assertEquals(0, atomic.load())
    }

    protected open suspend fun put() {
        val key = "test/p_" + snowflake.nextId()
        log.info { "put key: $key" }
        val obj = buildObj(key)
        assertFailsWith(RuntimeException::class) { obj.head() }
        try {
            val source = "hello world oss"
            val bytes = source.toByteArray()
            val hex = bytes.toMd5Hex()
            obj.put(
                live.lingting.framework.http.body.MemoryBody(bytes),
                _root_ide_package_.live.lingting.framework.aws.policy.Acl.PUBLIC_READ
            )
            val head = obj.head()
            assertNotNull(head)
            assertEquals(bytes.size.toLong(), head.contentSize().bytes)
            assertTrue("\"$hex\"".equals(head.etag(), ignoreCase = true))
            val string = client.get(obj.publicUrl()).bodyAsText()
            assertEquals(source, string)
            assertEquals(hex, string.toMd5Hex())
        } finally {
            obj.delete()
        }
    }

    protected open suspend fun multipart() {
        val ossBucket = buildBucket()
        val bo = ossBucket.use("test/m_b_" + snowflake.nextId())
        val uploadId = bo.multipartInit()
        val bm = ossBucket.multipartList {
            val params = it.params
            params.add("prefix", bo.key)
        }.items
        assertFalse(bm.isEmpty())
        assertTrue(bm.any { it.key == bo.key })
        assertTrue(bm.any { it.uploadId == uploadId })

        val list = ossBucket.multipartList().items
        if (list.isNotEmpty()) {
            list.forEach {
                val ossObject = ossBucket.use(it.key)
                ossObject.multipartCancel(it.uploadId)
            }
        }

        val snowflake = _root_ide_package_.live.lingting.framework.snowflake.Snowflake(0, 1)
        val key = "test/m_" + snowflake.nextId()
        val obj = buildObj(key)
        try {
            assertFailsWith(RuntimeException::class) { obj.head() }
            val source = "hello world\n".repeat(10000)
            val bytes = source.toByteArray()
            val hex = bytes.toMd5Hex()
            val task = obj.multipart(
                _root_ide_package_.live.lingting.framework.io.multipart.MemoryMultipartSource(bytes),
                1.bytes,
                _root_ide_package_.live.lingting.framework.async.async(10),
                _root_ide_package_.live.lingting.framework.aws.policy.Acl.PUBLIC_READ
            )
            assertTrue(task.isStarted)
            task.await()
            assertTrue(task.isCompleted)
            assertFalse(task.isFailed)
            val multipart = task.multipart
            assertTrue(multipart.partSize >= _root_ide_package_.live.lingting.framework.aws.AwsUtils.MULTIPART_MIN_PART_SIZE)
            val head = obj.head()
            assertNotNull(head)
            assertEquals(bytes.size.toLong(), head.contentSize().bytes)
            val string = client.get(obj.publicUrl()).bodyAsText()
            assertEquals(source, string)
            assertEquals(hex, string.toMd5Hex())
        } finally {
            obj.delete()
        }
    }

    protected open suspend fun listAndMeta() {
        val ossBucket = buildBucket()
        val key = "test/l_" + snowflake.nextId()
        val obj = ossBucket.use(key)
        try {
            val source = "hello world"
            val bytes = source.toByteArray()
            val md5 = bytes.toMd5Hex()
            val meta = _root_ide_package_.live.lingting.framework.aws.s3.AwsS3Meta()
            meta.add("md5", md5)
            meta.add("timestamp", _root_ide_package_.live.lingting.framework.time.DateTime.millis().toString())
            obj.put(
                live.lingting.framework.http.body.MemoryBody(bytes),
                _root_ide_package_.live.lingting.framework.aws.policy.Acl.PUBLIC_READ,
                meta
            )

            val lo = ossBucket.listObjects(key.substring(0, key.length - 3))
            assertTrue { lo.keyCount > 0 }
            val o = lo.contents.any { it.key == key }
            assertTrue(o)
            val lo2 = ossBucket.listObjects(key + "_222")
            assertTrue { lo2.keyCount == 0 }
            val o2 = lo2.contents.any { it.key == key }
            assertFalse(o2)

            val head = obj.head()
            assertNotNull(head)
            assertEquals(bytes.size.toLong(), head.contentSize().bytes)
            assertEquals(md5, head.first("md5"))
            assertEquals(meta.first("timestamp"), head.first("timestamp"))
            val string = client.get(obj.publicUrl()).bodyAsText()
            assertEquals(source, string)
            assertEquals(md5, string.toMd5Hex())
        } finally {
            obj.delete()
        }
    }

    protected open suspend fun pre() {
        val key = "test/pre_" + snowflake.nextId()
        log.info { "pre key: $key" }
        val obj = buildObj(key)

        val source = "hello world"
        val bytes = source.toByteArray()
        val hex = bytes.toMd5Hex()

        try {
            log.info { "ak: ${obj.ak}" }
            log.info { "sk: ${obj.sk}" }
            log.info { "token: ${obj.token}" }

            log.info { "=================put=================" }
            val prePutR = obj.prePut(
                _root_ide_package_.live.lingting.framework.aws.policy.Acl.PRIVATE,
                _root_ide_package_.live.lingting.framework.aws.s3.impl.S3Meta.empty().also {
                    it.put("pre", "true")
                })

            log.info { "put url: ${prePutR.url}" }

            client.put(prePutR.url) {
                headers.appendAll(prePutR.headers)
                body = source
            }.use { putR ->
                if (!putR.isOk) {
                    println(putR.bodyAsText())
                }
                assertTrue(putR.isOk)
            }

            _root_ide_package_.live.lingting.framework.concurrent.Await.wait(500.millis)
            client.get(obj.publicUrl()).use { getR ->
                assertFalse(getR.isOk)
            }

            log.info { "=================get=================" }
            val preGet = obj.preGet()
            log.info { "get url: ${preGet.url}" }
            client.get(preGet.url) {

            }.use { getR ->
                val string = getR.bodyAsText()
                if (!getR.isOk) {
                    println(string)
                }
                assertTrue(getR.isOk)
                assertEquals(source, string)
                assertEquals(hex, string.toMd5Hex())
            }

        } finally {
            log.info { "=================delete=================" }
            obj.delete()
        }
    }
}
