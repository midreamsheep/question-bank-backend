package indi.midreamsheep.vegetable.backend.features.user.domain.command;

/**
 * 用户注册命令。
 *
 * @param username 用户名
 * @param password 密码
 * @param nickname 昵称
 */
public record UserRegisterCommand(
        String username,
        String password,
        String nickname
) {
}
