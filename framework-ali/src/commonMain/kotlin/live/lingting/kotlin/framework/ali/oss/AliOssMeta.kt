package live.lingting.kotlin.framework.ali.oss

import live.lingting.kotlin.framework.ali.AliUtils
import live.lingting.kotlin.framework.aws.s3.impl.S3Meta
import live.lingting.kotlin.framework.value.MultiValue
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2025/6/4 20:55
 */
class AliOssMeta @JvmOverloads constructor(source: MultiValue<String, String, out Collection<String>>? = null) :
    S3Meta(AliUtils.HEADER_PREFIX_META) {

    init {
        source?.run { from(this) }
    }

}
