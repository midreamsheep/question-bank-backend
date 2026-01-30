package indi.midreamsheep.vegetable.backend.infrastructure.comment;

import indi.midreamsheep.vegetable.backend.features.comment.domain.ProblemCommentDomainService;
import indi.midreamsheep.vegetable.backend.features.comment.domain.port.ProblemCommentRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.problem.domain.port.ProblemRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 评论领域服务装配。
 */
@Configuration
public class ProblemCommentDomainConfig {

    /**
     * 构造评论领域服务。
     *
     * @param problemCommentRepositoryPort 评论仓储端口
     * @param problemRepositoryPort 题目仓储端口
     * @return 评论领域服务
     */
    @Bean
    public ProblemCommentDomainService problemCommentDomainService(
            ProblemCommentRepositoryPort problemCommentRepositoryPort,
            ProblemRepositoryPort problemRepositoryPort
    ) {
        return new ProblemCommentDomainService(problemCommentRepositoryPort, problemRepositoryPort);
    }
}

