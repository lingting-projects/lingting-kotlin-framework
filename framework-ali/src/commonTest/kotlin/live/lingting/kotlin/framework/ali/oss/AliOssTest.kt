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

    var sts: live.lingting.framework.ali.sts.AliSts? = null

    var useSts = true

    fun before() {
        sts = AliBasic.sts()
    }

    @Test
    fun test() = runTest {
        before()
        run()
    }

    override suspend fun buildObj(key: String): live.lingting.framework.ali.oss.AliOssObject =
        if (useSts) sts!!.ossObject(
            properties as live.lingting.framework.ali.properties.AliOssProperties,
            key
        ) else _root_ide_package_.live.lingting.framework.ali.oss.AliOssObject(
            AliBasic.ossProperties(),
            key
        )

    override suspend fun buildBucket(): live.lingting.framework.ali.oss.AliOssBucket =
        if (useSts) sts!!.ossBucket(properties as live.lingting.framework.ali.properties.AliOssProperties) else _root_ide_package_.live.lingting.framework.ali.oss.AliOssBucket(
            AliBasic.ossProperties()
        )

    override fun properties(): live.lingting.framework.aws.properties.S3Properties =
        if (useSts) AliBasic.ossStsProperties() else AliBasic.ossProperties()

    override suspend fun pre() {
        // 仅在非全球加速时测试预签名
        if (properties.region != _root_ide_package_.live.lingting.framework.ali.AliUtils.REGION_ACCELERATE) {
            super.pre()
        }
    }

}
