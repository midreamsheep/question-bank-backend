package indi.midreamsheep.vegetable.backend.infrastructure.problem;

import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemDomainService;
import indi.midreamsheep.vegetable.backend.features.problem.domain.port.ProblemRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.tag.domain.TagDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 题目领域服务装配。
 */
@Configuration
public class ProblemDomainConfig {

    /**
     * 构造题目领域服务。
     *
     * @param problemRepositoryPort 题目仓储端口
     * @param tagDomainService 标签领域服务
     * @return 题目领域服务
     */
    @Bean
    public ProblemDomainService problemDomainService(
            ProblemRepositoryPort problemRepositoryPort,
            TagDomainService tagDomainService
    ) {
        return new ProblemDomainService(problemRepositoryPort, tagDomainService);
    }
}
