package live.lingting.framework.aws.sts

/**
 * @author lingting 2025/6/3 14:47
 */
abstract class AwsStsRequest : live.lingting.framework.aws.AwsRequest() {

    override fun version(): String = "2011-06-15"

}
