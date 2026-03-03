package live.lingting.kotlin.framework.aws.s3

import io.ktor.http.Headers
import live.lingting.kotlin.framework.aws.AwsUtils
import live.lingting.kotlin.framework.aws.s3.impl.S3Meta
import live.lingting.kotlin.framework.http.header.HttpHeaders
import live.lingting.kotlin.framework.http.util.HttpHeadersUtils.toHttpHeaders
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2025/1/15 11:03
 */
class AwsS3Meta : S3Meta {

    @JvmOverloads
    constructor(source: HttpHeaders? = null) : super(AwsUtils.HEADER_PREFIX_META) {
        source?.run { from(this) }
    }

    constructor(source: Headers?) : this(source?.toHttpHeaders())

}
