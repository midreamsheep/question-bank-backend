package indi.midreamsheep.vegetable.backend.features.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求 DTO。
 *
 * @param username 用户名
 * @param password 密码
 */
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
