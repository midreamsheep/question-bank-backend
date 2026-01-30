package indi.midreamsheep.vegetable.backend.features.user.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.user.domain.UserStatus;

/**
 * 用户资料响应 DTO。
 *
 * @param id 用户ID
 * @param username 用户名
 * @param nickname 昵称
 * @param avatarFileId 头像文件ID
 * @param status 状态
 */
public record UserProfileResponse(
        long id,
        String username,
        String nickname,
        Long avatarFileId,
        UserStatus status
) {
}
