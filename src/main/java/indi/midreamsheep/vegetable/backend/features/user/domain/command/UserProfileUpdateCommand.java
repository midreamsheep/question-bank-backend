package indi.midreamsheep.vegetable.backend.features.user.domain.command;

/**
 * 用户资料更新命令。
 *
 * @param id 用户ID
 * @param nickname 昵称
 * @param avatarFileId 头像文件ID
 */
public record UserProfileUpdateCommand(
        long id,
        String nickname,
        Long avatarFileId
) {
}
