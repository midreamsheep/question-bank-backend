package indi.midreamsheep.vegetable.backend.features.auth.domain.port;

/**
 * 认证凭证校验端口：用于校验用户名/密码是否有效。
 */
public interface CredentialPort {

    /**
     * 校验用户名与密码并返回用户ID。
     *
     * @param username 用户名
     * @param password 密码（明文或已处理形式由实现方约定）
     * @return 通过校验时返回用户ID，否则为空
     */
    java.util.Optional<Long> verifyAndGetUserId(String username, String password);
}
