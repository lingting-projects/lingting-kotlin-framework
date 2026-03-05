package live.lingting.kotlin.framework.ali.oss

import live.lingting.kotlin.framework.aws.s3.AwsS3Client
import live.lingting.kotlin.framework.aws.s3.interfaces.AwsS3Delegation
import live.lingting.kotlin.framework.util.LoggerUtils.logger

/**
 * @author lingting 2024-09-19 22:05
 */
abstract class AliOss<C : AwsS3Client> protected constructor(protected val client: C) : AwsS3Delegation<C> {

    protected open val log = logger()

    init {
        client.listener = AliOssListener(client)
    }

    override fun delegation(): C {
        return client
    }


}
