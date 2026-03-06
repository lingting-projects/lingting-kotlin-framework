package live.lingting.framework.aws.s3

import io.ktor.http.Headers
import live.lingting.framework.http.util.HttpHeadersUtils.to
import kotlin.jvm.JvmOverloads

/**
 * @author lingting 2025/1/15 11:03
 */
class AwsS3Meta : live.lingting.framework.aws.s3.impl.S3Meta {

    @JvmOverloads
    constructor(source: live.lingting.framework.http.header.HttpHeaders? = null) : super(_root_ide_package_.live.lingting.framework.aws.AwsUtils.HEADER_PREFIX_META) {
        source?.run { from(this) }
    }

    constructor(source: Headers?) : this(source?.to())

}
