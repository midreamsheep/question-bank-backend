package indi.midreamsheep.vegetable.backend.features.user.domain.model;

import indi.midreamsheep.vegetable.backend.features.user.domain.UserStatus;

import java.time.LocalDateTime;

/**
 * 管理端用户详情数据。
 *
 * @param id 用户ID
 * @param username 用户名
 * @param nickname 昵称
 * @param avatarFileId 头像文件ID
 * @param status 状态
 * @param lastLoginAt 最近登录时间
 * @param createdAt 创建时间
 */
public record UserAdminDetailData(
        long id,
        String username,
        String nickname,
        Long avatarFileId,
        UserStatus status,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt
) {
}
