package live.lingting.framework.aws.s3.interfaces

/**
 * @author lingting 2024-09-19 22:06
 */
interface AwsS3Delegation<C : live.lingting.framework.aws.s3.AwsS3> {

    fun delegation(): C

    val ak
        get() = delegation().properties.ak

    val sk
        get() = delegation().properties.sk

    val token
        get() = delegation().properties.token

}
