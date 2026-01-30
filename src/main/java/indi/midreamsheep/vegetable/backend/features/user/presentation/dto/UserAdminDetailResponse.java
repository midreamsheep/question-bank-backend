package indi.midreamsheep.vegetable.backend.features.user.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.role.presentation.dto.RoleResponse;
import indi.midreamsheep.vegetable.backend.features.user.domain.UserStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理端用户详情响应 DTO。
 *
 * @param id 用户ID
 * @param username 用户名
 * @param nickname 昵称
 * @param avatarFileId 头像文件ID
 * @param status 状态
 * @param lastLoginAt 最近登录时间
 * @param createdAt 创建时间
 * @param roles 角色列表
 */
public record UserAdminDetailResponse(
        long id,
        String username,
        String nickname,
        Long avatarFileId,
        UserStatus status,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        List<RoleResponse> roles
) {
}
