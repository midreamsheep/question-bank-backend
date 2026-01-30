package indi.midreamsheep.vegetable.backend.features.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户修改密码请求 DTO。
 *
 * @param oldPassword 旧密码
 * @param newPassword 新密码
 */
public record UserPasswordChangeRequest(
        @NotBlank String oldPassword,
        @NotBlank @Size(min = 6, max = 64) String newPassword
) {
}
