package indi.midreamsheep.vegetable.backend.features.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 注册请求 DTO。
 *
 * @param username 用户名
 * @param password 密码
 * @param nickname 昵称
 */
public record RegisterRequest(
        @NotBlank @Size(max = 64) String username,
        @NotBlank @Size(min = 6, max = 64) String password,
        @Size(max = 64) String nickname
) {
}
