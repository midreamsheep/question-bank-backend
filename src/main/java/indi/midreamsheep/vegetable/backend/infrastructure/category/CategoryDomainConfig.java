package indi.midreamsheep.vegetable.backend.infrastructure.category;

import indi.midreamsheep.vegetable.backend.features.category.domain.CategoryDomainService;
import indi.midreamsheep.vegetable.backend.features.category.domain.port.CategoryRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 分类领域服务装配。
 */
@Configuration
public class CategoryDomainConfig {

    /**
     * 构造分类领域服务。
     *
     * @param categoryRepositoryPort 分类仓储端口
     * @return 分类领域服务
     */
    @Bean
    public CategoryDomainService categoryDomainService(CategoryRepositoryPort categoryRepositoryPort) {
        return new CategoryDomainService(categoryRepositoryPort);
    }
}
