package indi.midreamsheep.vegetable.backend.infrastructure.user;

import indi.midreamsheep.vegetable.backend.features.user.domain.UserDomainService;
import indi.midreamsheep.vegetable.backend.features.user.domain.port.UserRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.role.domain.port.RoleRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 用户领域服务装配。
 */
@Configuration
public class UserDomainConfig {

    /**
     * 构造用户领域服务。
     *
     * @param userRepositoryPort 用户仓储端口
     * @param passwordEncoder 密码编码器
     * @return 用户领域服务
     */
    @Bean
    public UserDomainService userDomainService(
            UserRepositoryPort userRepositoryPort,
            PasswordEncoder passwordEncoder,
            RoleRepositoryPort roleRepositoryPort
    ) {
        return new UserDomainService(userRepositoryPort, passwordEncoder, roleRepositoryPort);
    }
}
