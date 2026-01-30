package indi.midreamsheep.vegetable.backend.features.user.domain.command;

import indi.midreamsheep.vegetable.backend.features.user.domain.UserStatus;

/**
 * 用户创建命令（持久化输入）。
 *
 * @param username 用户名
 * @param passwordHash 密码哈希
 * @param nickname 昵称
 * @param avatarFileId 头像文件ID
 * @param status 状态
 */
public record UserCreateCommand(
        String username,
        String passwordHash,
        String nickname,
        Long avatarFileId,
        UserStatus status
) {
}
