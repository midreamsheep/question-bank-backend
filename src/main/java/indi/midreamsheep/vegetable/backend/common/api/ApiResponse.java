package indi.midreamsheep.vegetable.backend.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一接口响应体。
 *
 * @param code 业务状态码（约定：0 表示成功）
 * @param message 提示信息
 * @param data 响应数据
 * @param <T> 数据类型
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        int code,
        String message,
        T data
) {

    /**
     * 成功响应（携带数据）。
     *
     * @param data 响应数据
     * @return 统一响应体
     * @param <T> 数据类型
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, "OK", data);
    }

    /**
     * 成功响应（不携带数据）。
     *
     * @return 统一响应体
     */
    public static ApiResponse<Void> ok() {
        return ok(null);
    }

    /**
     * 失败响应（不携带数据）。
     *
     * @param code 业务状态码
     * @param message 错误信息
     * @return 统一响应体
     */
    public static ApiResponse<Void> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
