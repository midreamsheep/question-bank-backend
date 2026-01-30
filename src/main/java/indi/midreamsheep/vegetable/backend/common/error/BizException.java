package indi.midreamsheep.vegetable.backend.common.error;

/**
 * 业务异常：用于表达可预期、可处理的业务错误。
 */
public class BizException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 构造业务异常（使用错误码默认消息）。
     *
     * @param errorCode 错误码
     */
    public BizException(ErrorCode errorCode) {
        super(errorCode.defaultMessage());
        this.errorCode = errorCode;
    }

    /**
     * 构造业务异常（指定消息）。
     *
     * @param errorCode 错误码
     * @param message 错误消息
     */
    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码。
     *
     * @return 错误码
     */
    public ErrorCode errorCode() {
        return errorCode;
    }
}
