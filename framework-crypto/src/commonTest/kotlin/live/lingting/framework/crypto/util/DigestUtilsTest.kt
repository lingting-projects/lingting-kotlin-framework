package live.lingting.framework.crypto.util

import kotlinx.io.Buffer
import live.lingting.framework.crypto.util.DigestUtils.toMd5Hex
import live.lingting.framework.crypto.util.DigestUtils.toSha1Hex
import live.lingting.framework.crypto.util.DigestUtils.toSha256Hex
import live.lingting.framework.crypto.util.DigestUtils.toSha512Hex
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author lingting 2026/3/17 18:48
 */
class DigestUtilsTest {
    private val inputString = "hello world"
    private val inputBytes = inputString.encodeToByteArray()

    // 预期的标准哈希值 (可通过 printf "hello world" | md5sum 等命令校验)
    private val expectedMd5 = "5eb63bbbe01eeed093cb22bb8f5acdc3"
    private val expectedSha1 = "2aae6c35c94fcfb415dbe95f408b9ce91ee846ed"
    private val expectedSha256 = "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9"
    private val expectedSha512 =
        "309ecc489c12d6eb4cc40f50c902f2b4d0ed77ee511a7c7a9bcd3ca86d4cd86f989dd35bc5ff499670da34255b45b0cfd830e81f605dcf7dc5542e93ae9cd76f"

    @Test
    fun test() {
        testMd5()
        testSha1()
        testSha256()
        testSha512()
    }

    fun testMd5() {
        // String 扩展
        assertEquals(expectedMd5, inputString.toMd5Hex())
        // ByteArray 扩展
        assertEquals(expectedMd5, inputBytes.toMd5Hex())
        // RawSource 扩展
        val source = Buffer().apply { write(inputBytes) }
        assertEquals(expectedMd5, source.toMd5Hex())
    }

    fun testSha1() {
        assertEquals(expectedSha1, inputString.toSha1Hex())
        assertEquals(expectedSha1, inputBytes.toSha1Hex())

        val source = Buffer().apply { write(inputBytes) }
        assertEquals(expectedSha1, source.toSha1Hex())
    }

    fun testSha256() {
        assertEquals(expectedSha256, inputString.toSha256Hex())
        assertEquals(expectedSha256, inputBytes.toSha256Hex())

        val source = Buffer().apply { write(inputBytes) }
        assertEquals(expectedSha256, source.toSha256Hex())
    }

    fun testSha512() {
        assertEquals(expectedSha512, inputString.toSha512Hex())
        assertEquals(expectedSha512, inputBytes.toSha512Hex())

        val source = Buffer().apply { write(inputBytes) }
        assertEquals(expectedSha512, source.toSha512Hex())
    }

}
