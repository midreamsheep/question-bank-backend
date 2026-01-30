package indi.midreamsheep.vegetable.backend.features.user.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.user.domain.UserStatus;
import jakarta.validation.constraints.NotNull;

/**
 * 用户状态更新请求 DTO。
 *
 * @param status 状态
 */
public record UserStatusUpdateRequest(
        @NotNull UserStatus status
) {
}
