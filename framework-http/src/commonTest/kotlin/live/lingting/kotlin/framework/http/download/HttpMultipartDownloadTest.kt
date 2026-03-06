package live.lingting.framework.http.download

import kotlinx.coroutines.test.runTest
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import live.lingting.framework.crypto.util.DigestUtils.toMd5Hex
import live.lingting.framework.http.donwload.HttpMultipartDownload
import live.lingting.framework.util.DurationUtils.minutes
import live.lingting.framework.util.ValueUtils
import okio.FileSystem
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author lingting 2026/3/2 16:55
 */
class HttpMultipartDownloadTest {

    val url =
        "https://maven.aliyun.com/repository/central/live/lingting/components/component-validation/0.0.1/component-validation-0.0.1.pom"

    val md5 = "2ce519cf7373a533e1fd297edb9ad1c3"

    @Test
    fun `download memory`() = runTest {
        val download = donwload.HttpMultipartDownload.build {
            url(url)
        }
        download.start()
        download.await(1.minutes)
        val bytes = download.source().bytes()
        assertEquals(md5, bytes.toMd5Hex())
    }

    @Test
    fun `download file`() = runTest {
        val tmpDir = Path(FileSystem.SYSTEM_TEMPORARY_DIRECTORY.toString())
        val file = Path(tmpDir, ".${ValueUtils.simpleUuid()}")
        val download = donwload.HttpMultipartDownload.build {
            url(url)
            sink(file)
        }
        download.start()
        download.await(1.minutes)
        val bytes = download.source().bytes()
        assertEquals(md5, bytes.toMd5Hex())
        assertEquals(md5, SystemFileSystem.source(file).buffered().readByteArray().toMd5Hex())
    }

}
