package indi.midreamsheep.vegetable.backend.infrastructure.favorite;

import indi.midreamsheep.vegetable.backend.features.collection.domain.port.CollectionRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.favorite.domain.FavoriteDomainService;
import indi.midreamsheep.vegetable.backend.features.favorite.domain.port.FavoriteCollectionRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.favorite.domain.port.FavoriteProblemRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.problem.domain.port.ProblemRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 收藏领域服务装配。
 */
@Configuration
public class FavoriteDomainConfig {

    /**
     * 构造收藏领域服务。
     *
     * @param favoriteProblemRepositoryPort 收藏题目仓储端口
     * @param favoriteCollectionRepositoryPort 收藏题单仓储端口
     * @param problemRepositoryPort 题目仓储端口
     * @param collectionRepositoryPort 题单仓储端口
     * @return 收藏领域服务
     */
    @Bean
    public FavoriteDomainService favoriteDomainService(
            FavoriteProblemRepositoryPort favoriteProblemRepositoryPort,
            FavoriteCollectionRepositoryPort favoriteCollectionRepositoryPort,
            ProblemRepositoryPort problemRepositoryPort,
            CollectionRepositoryPort collectionRepositoryPort
    ) {
        return new FavoriteDomainService(
                favoriteProblemRepositoryPort,
                favoriteCollectionRepositoryPort,
                problemRepositoryPort,
                collectionRepositoryPort
        );
    }
}
