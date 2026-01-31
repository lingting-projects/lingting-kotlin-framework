package live.lingting.kotlin.framework.exception

/**
 * @author lingting 2026/1/30 16:12
 */
class TimeoutException : RuntimeException {

    constructor() : super()

    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)

}
