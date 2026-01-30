package indi.midreamsheep.vegetable.backend.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 管理员配置项。
 *
 * @param userIds 管理员用户ID列表
 */
@ConfigurationProperties(prefix = "security.admin")
public record AdminProperties(
        List<Long> userIds
) {
}
