package indi.midreamsheep.vegetable.backend.infrastructure.daily;

import indi.midreamsheep.vegetable.backend.features.daily.domain.DailyProblemDomainService;
import indi.midreamsheep.vegetable.backend.features.daily.domain.port.DailyProblemRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.problem.domain.port.ProblemRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 每日一题领域服务装配。
 */
@Configuration
public class DailyProblemDomainConfig {

    /**
     * 构造每日一题领域服务。
     *
     * @param dailyProblemRepositoryPort 每日一题仓储端口
     * @param problemRepositoryPort 题目仓储端口
     * @return 每日一题领域服务
     */
    @Bean
    public DailyProblemDomainService dailyProblemDomainService(
            DailyProblemRepositoryPort dailyProblemRepositoryPort,
            ProblemRepositoryPort problemRepositoryPort
    ) {
        return new DailyProblemDomainService(dailyProblemRepositoryPort, problemRepositoryPort);
    }
}
