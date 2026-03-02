package live.lingting.kotlin.framework.http.donwload

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.contentLength
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.cancel
import kotlinx.io.Sink
import kotlinx.io.files.Path
import live.lingting.kotlin.framework.async.Async
import live.lingting.kotlin.framework.async.async
import live.lingting.kotlin.framework.data.DataSize
import live.lingting.kotlin.framework.http.api.ApiClient
import live.lingting.kotlin.framework.io.multipart.FileMultipartSink
import live.lingting.kotlin.framework.io.multipart.MemoryMultipartSink
import live.lingting.kotlin.framework.io.multipart.MultipartSink
import live.lingting.kotlin.framework.multipart.Multipart
import live.lingting.kotlin.framework.multipart.task.PartTask
import live.lingting.kotlin.framework.multipart.task.SinkMultipartTask
import live.lingting.kotlin.framework.util.DataSizeUtils.bytes
import live.lingting.kotlin.framework.util.DataSizeUtils.mb
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2026/2/28 17:53
 */
open class HttpMultipartDownload : SinkMultipartTask {

    companion object {

        @JvmStatic
        fun builder() = Builder()

        @JvmStatic
        suspend fun build(block: Builder.() -> Unit) = Builder().apply(block).build()

        @JvmStatic
        fun build(size: DataSize, block: Builder.() -> Unit) = Builder().apply(block).build(size)

    }

    val url: Url

    protected val client: HttpClient

    var readSize = 1.mb

    @JvmOverloads
    protected constructor(
        url: Url,
        sink: MultipartSink,
        multipart: Multipart,
        async: Async = async(),
        client: HttpClient = ApiClient.defaultClient,
    ) : super(sink, multipart, async) {
        this.client = client
        this.url = url
    }

    @JvmOverloads
    protected constructor(
        url: Url,
        path: Path,
        multipart: Multipart,
        async: Async = async(),
        client: HttpClient = ApiClient.defaultClient,
    ) : this(url, FileMultipartSink(path), multipart, async, client)

    @JvmOverloads
    protected constructor(
        url: Url,
        multipart: Multipart,
        async: Async = async(),
        client: HttpClient = ApiClient.defaultClient,
    ) : this(url, MemoryMultipartSink(), multipart, async, client)

    override suspend fun onPart(task: PartTask, partSink: Sink) {
        val part = task.part
        val response = client.get(url) {
            headers[HttpHeaders.Range] = "bytes=${part.start.bytes}-${part.end.bytes}"
        }
        try {
            val httpStatus = response.status.value
            require(httpStatus == 206) { "分片下载异常! url: $url; part: $part; httpStatus: $httpStatus" }
            val channel = response.bodyAsChannel()
            val buffer = ByteArray(readSize.bytes.toInt())
            while (!channel.isClosedForRead) {
                val read = channel.readAvailable(buffer)
                if (read > 0) {
                    partSink.write(buffer, 0, read)
                }
            }
            partSink.flush()
        } finally {
            response.cancel()
        }
    }

    class Builder {

        private var url: Url? = null

        private var client: HttpClient = ApiClient.defaultClient

        private var async: Async = async()

        private var retryCount: Int = 3

        private var partSize: DataSize = 3.mb

        private var sink: MultipartSink = MemoryMultipartSink()

        fun url(url: String) = apply { this.url = Url(url) }

        fun url(url: Url) = apply { this.url = url }

        fun client(client: HttpClient) = apply { this.client = client }

        fun async(async: Async) = apply { this.async = async }

        fun retry(count: Int) = apply { this.retryCount = count }

        fun partSize(size: DataSize) = apply { this.partSize = size }

        fun sink(path: String) = sink(Path(path))

        fun sink(path: Path) = apply {
            this.sink = FileMultipartSink(path)
        }

        fun sink(sink: MultipartSink) = apply { this.sink = sink }

        suspend fun build(): HttpMultipartDownload {
            val targetUrl = requireNotNull(url) { "URL 不能为空" }
            val headResponse = client.head(targetUrl)
            val bytes = headResponse.contentLength()
                ?: throw IllegalStateException("无法获取文件大小! url: $targetUrl")
            return build(bytes.bytes)
        }

        fun build(size: DataSize): HttpMultipartDownload {
            val targetUrl = requireNotNull(url) { "URL 不能为空" }

            val multipart = Multipart.builder().also {
                it.size(size)
                it.partSize(partSize)
            }.build()

            require(multipart.parts.isNotEmpty()) { "至少要有一个可下载分片!" }
            val download = HttpMultipartDownload(
                targetUrl,
                sink,
                multipart,
                async,
                client,
            )
            download.retryMaxCount = retryCount
            return download
        }
    }

}
