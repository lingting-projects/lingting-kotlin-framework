package live.lingting.framework.exception

import live.lingting.framework.api.ResultCode
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * @author lingting 2022/9/22 12:11
 */
open class BizException : RuntimeException {

    companion object {

        @JvmStatic
        @JvmOverloads
        fun format(result: ResultCode, message: String? = null): String {
            return "[${result.code}] ${message ?: result.message}"
        }

    }

    val result: ResultCode

    val code: Int

    val rawMessage: String

    constructor(result: ResultCode) : this(result, result.message)

    constructor(result: ResultCode, e: Exception? = null) : this(result, null, e)

    constructor(result: ResultCode, message: String?, e: Exception? = null) : super(format(result, message), e) {
        this.result = result
        this.code = result.code
        this.rawMessage = message ?: result.message
    }

    override val message: String
        get() = super.message ?: format(result)

}
