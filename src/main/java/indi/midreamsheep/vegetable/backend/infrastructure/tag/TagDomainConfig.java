package indi.midreamsheep.vegetable.backend.infrastructure.tag;

import indi.midreamsheep.vegetable.backend.features.tag.domain.TagDomainService;
import indi.midreamsheep.vegetable.backend.features.tag.domain.port.TagRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 标签领域服务装配。
 */
@Configuration
public class TagDomainConfig {

    /**
     * 构造标签领域服务。
     *
     * @param tagRepositoryPort 标签仓储端口
     * @return 标签领域服务
     */
    @Bean
    public TagDomainService tagDomainService(TagRepositoryPort tagRepositoryPort) {
        return new TagDomainService(tagRepositoryPort);
    }
}
