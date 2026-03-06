package live.lingting.framework.ali.oss

import kotlinx.coroutines.test.runTest
import live.lingting.framework.ali.AliBasic
import live.lingting.framework.ali.AliUtils
import live.lingting.framework.ali.properties.AliOssProperties
import live.lingting.framework.ali.sts.AliSts
import live.lingting.framework.aws.properties.S3Properties
import live.lingting.framework.aws.s3.S3BasicTest
import kotlin.test.Test

/**
 * @author lingting 2024-09-18 14:29
 */
class AliOssTest : S3BasicTest() {

    var sts: AliSts? = null

    var useSts = true

    fun before() {
        sts = AliBasic.sts()
    }

    @Test
    fun test() = runTest {
        before()
        run()
    }

    override suspend fun buildObj(key: String): AliOssObject =
        if (useSts) sts!!.ossObject(
            properties as AliOssProperties,
            key
        ) else AliOssObject(
            AliBasic.ossProperties(),
            key
        )

    override suspend fun buildBucket(): AliOssBucket =
        if (useSts) sts!!.ossBucket(properties as AliOssProperties) else AliOssBucket(
            AliBasic.ossProperties()
        )

    override fun properties(): S3Properties =
        if (useSts) AliBasic.ossStsProperties() else AliBasic.ossProperties()

    override suspend fun pre() {
        // 仅在非全球加速时测试预签名
        if (properties.region != AliUtils.REGION_ACCELERATE) {
            super.pre()
        }
    }

}
