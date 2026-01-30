package indi.midreamsheep.vegetable.backend.features.auth.data;

import indi.midreamsheep.vegetable.backend.features.auth.domain.port.CredentialPort;
import indi.midreamsheep.vegetable.backend.features.user.domain.UserStatus;
import indi.midreamsheep.vegetable.backend.features.user.domain.model.UserCredentialData;
import indi.midreamsheep.vegetable.backend.features.user.domain.port.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 基于数据库的凭证校验实现。
 */
@Component
public class UserCredentialAdapter implements CredentialPort {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    /**
     * 构造凭证校验适配器。
     *
     * @param userMapper 用户 Mapper
     * @param passwordEncoder 密码编码器
     */
    public UserCredentialAdapter(UserRepositoryPort userRepositoryPort, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 校验用户名与密码并返回用户ID。
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户ID（校验失败返回空）
     */
    @Override
    public Optional<Long> verifyAndGetUserId(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return Optional.empty();
        }
        String normalizedUsername = username.trim();
        UserCredentialData credential = userRepositoryPort.findCredentialByUsername(normalizedUsername)
                .orElse(null);
        if (credential == null || credential.passwordHash() == null) {
            return Optional.empty();
        }
        if (credential.status() == UserStatus.DISABLED) {
            return Optional.empty();
        }
        if (!passwordEncoder.matches(password, credential.passwordHash())) {
            return Optional.empty();
        }
        updateLastLogin(credential.id());
        return Optional.of(credential.id());
    }

    /**
     * 更新最近登录时间。
     *
     * @param userId 用户ID
     */
    private void updateLastLogin(long userId) {
        LocalDateTime now = LocalDateTime.now();
        userRepositoryPort.updateLastLogin(userId, now);
    }
}
