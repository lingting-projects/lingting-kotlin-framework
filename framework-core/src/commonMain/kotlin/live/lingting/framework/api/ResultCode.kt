package live.lingting.framework.api


/**
 * @author lingting 2022/9/19 13:55
 */
interface ResultCode {
    /**
     * 返回的code
     * @return int
     */
    val code: Int

    /**
     * 返回消息
     * @return string
     */
    val message: String

    /**
     * 更新消息
     */
    fun with(message: String): ResultCode {
        val code = code
        return object : ResultCode {
            override val code: Int
                get() = code

            override val message: String
                get() = message
        }
    }

}
