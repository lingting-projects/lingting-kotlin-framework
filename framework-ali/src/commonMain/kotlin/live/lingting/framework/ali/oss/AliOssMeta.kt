package live.lingting.framework.ali.oss

import live.lingting.framework.ali.AliUtils
import live.lingting.framework.aws.s3.impl.S3Meta
import live.lingting.framework.value.MultiValue
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2025/6/4 20:55
 */
class AliOssMeta @JvmOverloads constructor(source: live.lingting.framework.value.MultiValue<String, String, out Collection<String>>? = null) :
    live.lingting.framework.aws.s3.impl.S3Meta(_root_ide_package_.live.lingting.framework.ali.AliUtils.HEADER_PREFIX_META) {

    init {
        source?.run { from(this) }
    }

}
