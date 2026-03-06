package live.lingting.framework.ali.oss

import live.lingting.framework.aws.s3.AwsS3
import live.lingting.framework.aws.s3.interfaces.AwsS3Delegation
import live.lingting.framework.util.LoggerUtils.logger

/**
 * @author lingting 2024-09-19 22:05
 */
abstract class AliOss<C : live.lingting.framework.aws.s3.AwsS3> protected constructor(protected val client: C) :
    live.lingting.framework.aws.s3.interfaces.AwsS3Delegation<C> {

    protected open val log = logger()

    init {
        client.listener = _root_ide_package_.live.lingting.framework.ali.oss.AliOssListener(client)
    }

    override fun delegation(): C {
        return client
    }


}
