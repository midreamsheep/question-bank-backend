package indi.midreamsheep.vegetable.backend.infrastructure.problemtype;

import indi.midreamsheep.vegetable.backend.features.problemtype.domain.ProblemTypeDomainService;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.port.ProblemTypeRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 题型领域服务装配。
 */
@Configuration
public class ProblemTypeDomainConfig {

    /**
     * 构造题型领域服务。
     *
     * @param problemTypeRepositoryPort 题型仓储端口
     * @return 题型领域服务
     */
    @Bean
    public ProblemTypeDomainService problemTypeDomainService(ProblemTypeRepositoryPort problemTypeRepositoryPort) {
        return new ProblemTypeDomainService(problemTypeRepositoryPort);
    }
}
