package indi.midreamsheep.vegetable.backend.common.error;

/**
 * 统一错误码定义（稳定整数，不随意变更）。
 */
public enum ErrorCode {
    /**
     * 请求参数不合法。
     */
    BAD_REQUEST(40000, "Bad request"),
    /**
     * 未认证（缺少或无效的身份凭证）。
     */
    UNAUTHORIZED(40100, "Unauthorized"),
    /**
     * 已认证但无权限访问资源。
     */
    FORBIDDEN(40300, "Forbidden"),
    /**
     * 资源不存在。
     */
    NOT_FOUND(40400, "Not found"),
    /**
     * 服务端内部错误。
     */
    /**
     * 服务不可用（例如依赖组件未启用或不可用）。
     */
    SERVICE_UNAVAILABLE(50300, "Service unavailable"),
    /**
     * 服务端内部错误。
     */
    INTERNAL_ERROR(50000, "Internal server error");

    private final int code;
    private final String defaultMessage;

    /**
     * 构造错误码。
     *
     * @param code 数值错误码
     * @param defaultMessage 默认错误消息
     */
    ErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    /**
     * 获取数值错误码。
     *
     * @return 数值错误码
     */
    public int code() {
        return code;
    }

    /**
     * 获取默认错误消息。
     *
     * @return 默认错误消息
     */
    public String defaultMessage() {
        return defaultMessage;
    }
}
