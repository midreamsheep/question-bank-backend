package indi.midreamsheep.vegetable.backend.infrastructure.like;

import indi.midreamsheep.vegetable.backend.features.like.domain.LikeDomainService;
import indi.midreamsheep.vegetable.backend.features.like.domain.LikeCommentDomainService;
import indi.midreamsheep.vegetable.backend.features.like.domain.port.LikeCommentRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.like.domain.port.LikeProblemRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.comment.domain.port.ProblemCommentRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.problem.domain.port.ProblemRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 点赞领域服务装配。
 */
@Configuration
public class LikeDomainConfig {

    /**
     * 构造点赞领域服务。
     *
     * @param likeProblemRepositoryPort 点赞题目仓储端口
     * @param problemRepositoryPort 题目仓储端口
     * @return 点赞领域服务
     */
    @Bean
    public LikeDomainService likeDomainService(
            LikeProblemRepositoryPort likeProblemRepositoryPort,
            ProblemRepositoryPort problemRepositoryPort
    ) {
        return new LikeDomainService(likeProblemRepositoryPort, problemRepositoryPort);
    }

    /**
     * 构造评论点赞领域服务。
     *
     * @param likeCommentRepositoryPort 评论点赞仓储端口
     * @param problemCommentRepositoryPort 评论仓储端口
     * @param problemRepositoryPort 题目仓储端口
     * @return 评论点赞领域服务
     */
    @Bean
    public LikeCommentDomainService likeCommentDomainService(
            LikeCommentRepositoryPort likeCommentRepositoryPort,
            ProblemCommentRepositoryPort problemCommentRepositoryPort,
            ProblemRepositoryPort problemRepositoryPort
    ) {
        return new LikeCommentDomainService(
                likeCommentRepositoryPort,
                problemCommentRepositoryPort,
                problemRepositoryPort
        );
    }
}
