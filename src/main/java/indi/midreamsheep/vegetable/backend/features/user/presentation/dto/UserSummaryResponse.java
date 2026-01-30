package indi.midreamsheep.vegetable.backend.features.user.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.user.domain.UserStatus;

import java.time.LocalDateTime;

/**
 * 用户摘要响应 DTO（管理端）。
 *
 * @param id 用户ID
 * @param username 用户名
 * @param nickname 昵称
 * @param status 状态
 * @param lastLoginAt 最近登录时间
 * @param createdAt 创建时间
 */
public record UserSummaryResponse(
        long id,
        String username,
        String nickname,
        UserStatus status,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
) {
}
