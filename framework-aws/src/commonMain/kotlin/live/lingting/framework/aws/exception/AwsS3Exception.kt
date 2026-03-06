package live.lingting.framework.aws.exception

/**
 * @author lingting 2024-09-19 15:14
 */
class AwsS3Exception : live.lingting.framework.aws.exception.AwsException {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

}
