package indi.midreamsheep.vegetable.backend.infrastructure.collection;

import indi.midreamsheep.vegetable.backend.features.collection.domain.CollectionDomainService;
import indi.midreamsheep.vegetable.backend.features.collection.domain.port.CollectionRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.problem.domain.port.ProblemRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 题单领域服务装配。
 */
@Configuration
public class CollectionDomainConfig {

    /**
     * 构造题单领域服务。
     *
     * @param collectionRepositoryPort 题单仓储端口
     * @param problemRepositoryPort 题目仓储端口
     * @return 题单领域服务
     */
    @Bean
    public CollectionDomainService collectionDomainService(
            CollectionRepositoryPort collectionRepositoryPort,
            ProblemRepositoryPort problemRepositoryPort
    ) {
        return new CollectionDomainService(collectionRepositoryPort, problemRepositoryPort);
    }
}
