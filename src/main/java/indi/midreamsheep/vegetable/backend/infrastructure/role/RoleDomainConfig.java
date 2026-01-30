package indi.midreamsheep.vegetable.backend.infrastructure.role;

import indi.midreamsheep.vegetable.backend.features.role.domain.RoleDomainService;
import indi.midreamsheep.vegetable.backend.features.role.domain.port.RoleRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 角色领域服务装配。
 */
@Configuration
public class RoleDomainConfig {

    /**
     * 构造角色领域服务。
     *
     * @param roleRepositoryPort 角色仓储端口
     * @return 角色领域服务
     */
    @Bean
    public RoleDomainService roleDomainService(RoleRepositoryPort roleRepositoryPort) {
        return new RoleDomainService(roleRepositoryPort);
    }
}
