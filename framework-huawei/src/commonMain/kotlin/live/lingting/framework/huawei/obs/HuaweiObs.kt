package live.lingting.framework.huawei.obs

import live.lingting.framework.aws.s3.AwsS3
import live.lingting.framework.aws.s3.interfaces.AwsS3Delegation
import live.lingting.framework.huawei.properties.HuaweiObsProperties

/**
 * @author lingting 2024-09-13 13:45
 */
abstract class HuaweiObs<C : AwsS3> protected constructor(protected val client: C) : AwsS3Delegation<C> {

    companion object {

        const val HEADER_PREFIX: String = "x-obs"

        const val HEADER_PREFIX_META = "$HEADER_PREFIX-meta-"

        const val HEADER_TOKEN = "$HEADER_PREFIX-security-token"
    }

    init {
        client.listener = HuaweiObsListener(this)
    }

    abstract val properties: HuaweiObsProperties

    override fun delegation(): C {
        return client
    }

}
