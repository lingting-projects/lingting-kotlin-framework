package live.lingting.framework.api

/**
 * @author lingting 2022/9/19 13:56
 */
enum class ApiResultCode(
    override val code: Int,
    override val message: String,
) : ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "Success"),

    /**
     * 参数异常
     */
    PARAMS_ERROR(400, "Params Error!"),

    /**
     * 授权异常, 身份异常
     */
    UNAUTHORIZED_ERROR(401, "Unauthorized Error!"),

    /**
     * 权限异常
     */
    FORBIDDEN_ERROR(403, "Forbidden Error!"),

    /**
     * 请求地址异常
     */
    NOT_FOUND_ERROR(404, "Path Not Found!"),

    /**
     * 重复请求
     */
    REPEAT_ERROR(409, "Repeat error!"),

    /**
     * 服务异常
     */
    SERVER_ERROR(500, "Server Error!"),

    ;

}
