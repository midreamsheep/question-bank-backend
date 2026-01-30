package indi.midreamsheep.vegetable.backend.features.user.domain.command;

/**
 * 用户修改密码命令。
 *
 * @param id 用户ID
 * @param oldPassword 旧密码
 * @param newPassword 新密码
 */
public record UserPasswordChangeCommand(
        long id,
        String oldPassword,
        String newPassword
) {
}
