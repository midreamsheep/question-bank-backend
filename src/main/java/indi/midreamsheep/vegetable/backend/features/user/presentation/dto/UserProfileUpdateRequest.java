package indi.midreamsheep.vegetable.backend.features.user.presentation.dto;

import jakarta.validation.constraints.Size;

/**
 * 用户资料更新请求 DTO。
 *
 * @param nickname 昵称
 * @param avatarFileId 头像文件ID
 */
public record UserProfileUpdateRequest(
        @Size(max = 64) String nickname,
        Long avatarFileId
) {
}
