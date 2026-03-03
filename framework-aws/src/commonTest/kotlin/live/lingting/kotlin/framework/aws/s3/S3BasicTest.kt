package live.lingting.kotlin.framework.aws.s3

import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.util.appendAll
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.CoroutineScope
import live.lingting.kotlin.framework.async.async
import live.lingting.kotlin.framework.aws.AwsUtils
import live.lingting.kotlin.framework.aws.policy.Acl
import live.lingting.kotlin.framework.aws.properties.S3Properties
import live.lingting.kotlin.framework.aws.s3.impl.S3Meta
import live.lingting.kotlin.framework.aws.s3.interfaces.AwsS3BucketDelegation
import live.lingting.kotlin.framework.aws.s3.interfaces.AwsS3ObjectDelegation
import live.lingting.kotlin.framework.concurrent.Await
import live.lingting.kotlin.framework.crypto.util.DigestUtils.toMd5Hex
import live.lingting.kotlin.framework.http.HttpClient
import live.lingting.kotlin.framework.http.util.HttpExtraUtils.use
import live.lingting.kotlin.framework.http.util.HttpUtils.isOk
import live.lingting.kotlin.framework.io.multipart.MemoryMultipartSource
import live.lingting.kotlin.framework.snowflake.Snowflake
import live.lingting.kotlin.framework.time.DateTime
import live.lingting.kotlin.framework.util.CoroutineUtils
import live.lingting.kotlin.framework.util.DataSizeUtils.bytes
import live.lingting.kotlin.framework.util.DurationUtils.millis
import live.lingting.kotlin.framework.util.IoUtils.source
import live.lingting.kotlin.framework.util.LoggerUtils.logger
import live.lingting.kotlin.framework.util.SystemUtils
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


/**
 * @author lingting 2025/8/27 11:35
 */
abstract class S3BasicTest {

    private val log = logger()

    private val snowflake = Snowflake(0, 0)

    abstract suspend fun buildObj(key: String): AwsS3ObjectDelegation

    abstract suspend fun buildBucket(): AwsS3BucketDelegation

    abstract fun properties(): S3Properties

    protected val properties = properties()

    protected var client = HttpClient.default()

    suspend fun CoroutineScope.run() {
        CoroutineUtils.switchScope(this)
        doTest()
        val domain = SystemUtils.getEnv("DOMAIN")
        if (!domain.isNullOrBlank()) {
            properties.domain = domain
            doTest()
        }
        val domain1 = SystemUtils.getEnv("DOMAIN1")
        if (!domain1.isNullOrBlank()) {
            properties.domain = domain1
            doTest()
        }
    }

    @OptIn(ExperimentalAtomicApi::class)
    protected open suspend fun doTest() {
        val async = async()
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
            obj.put(bytes.source(), Acl.PUBLIC_READ)
            val head = obj.head()
            assertNotNull(head)
            assertEquals(bytes.size.toLong(), head.contentLength())
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

        val snowflake = Snowflake(0, 1)
        val key = "test/m_" + snowflake.nextId()
        val obj = buildObj(key)
        try {
            assertFailsWith(RuntimeException::class) { obj.head() }
            val source = "hello world\n".repeat(10000)
            val bytes = source.toByteArray()
            val hex = bytes.toMd5Hex()
            val task = obj.multipart(
                MemoryMultipartSource(bytes),
                1.bytes,
                async(10),
                Acl.PUBLIC_READ
            )
            assertTrue(task.isStarted)
            task.await()
            assertTrue(task.isCompleted)
            assertFalse(task.isFailed)
            val multipart = task.multipart
            assertTrue(multipart.partSize >= AwsUtils.MULTIPART_MIN_PART_SIZE)
            val head = obj.head()
            assertNotNull(head)
            assertEquals(bytes.size.toLong(), head.contentLength())
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
            val meta = AwsS3Meta()
            meta.add("md5", md5)
            meta.add("timestamp", DateTime.millis().toString())
            obj.put(bytes.source(), Acl.PUBLIC_READ, meta)

            val lo = ossBucket.listObjects(key.substring(0, 4))
            assertTrue { lo.keyCount > 0 }
            val o = lo.contents.any { it.key == key }
            assertNotNull(o)
            val lo2 = ossBucket.listObjects(key + "_21")
            assertTrue { lo2.keyCount == 0 }
            val o2 = lo2.contents.any { it.key == key }
            assertNull(o2)

            val head = obj.head()
            assertNotNull(head)
            assertEquals(bytes.size.toLong(), head.contentLength())
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
            val prePutR = obj.prePut(Acl.PRIVATE, S3Meta.empty().also {
                it.put("pre", "true")
            })

            log.info { "put url: ${prePutR.url}" }

            client.put(prePutR.url) {
                headers.appendAll(prePutR.headers)
                setBody(source)
            }.use { putR ->
                if (!putR.isOk) {
                    println(putR.bodyAsText())
                }
                assertTrue(putR.isOk)
            }

            Await.wait(500.millis)
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
