package indi.midreamsheep.vegetable.backend.features.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 管理员重置密码请求 DTO。
 *
 * @param newPassword 新密码
 */
public record UserPasswordResetRequest(
        @NotBlank @Size(min = 6, max = 64) String newPassword
) {
}
