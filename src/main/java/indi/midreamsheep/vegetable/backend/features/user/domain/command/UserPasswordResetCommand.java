package indi.midreamsheep.vegetable.backend.features.user.domain.command;

/**
 * 用户密码重置命令（管理员）。
 *
 * @param id 用户ID
 * @param newPassword 新密码
 */
public record UserPasswordResetCommand(
        long id,
        String newPassword
) {
}
