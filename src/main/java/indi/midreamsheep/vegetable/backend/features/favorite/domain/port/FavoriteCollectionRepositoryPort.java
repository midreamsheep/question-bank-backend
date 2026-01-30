package indi.midreamsheep.vegetable.backend.features.favorite.domain.port;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;

/**
 * 用户收藏题单仓储端口。
 */
public interface FavoriteCollectionRepositoryPort {

    /**
     * 收藏题单（幂等）。
     *
     * @param userId 用户ID
     * @param collectionId 题单ID
     * @return 是否发生状态变更（从未收藏/取消收藏 -> 收藏）
     */
    boolean add(long userId, long collectionId);

    /**
     * 取消收藏（幂等）。
     *
     * @param userId 用户ID
     * @param collectionId 题单ID
     * @return 是否发生状态变更（收藏 -> 取消收藏）
     */
    boolean remove(long userId, long collectionId);

    /**
     * 收藏题单 ID 列表（分页，按收藏时间倒序）。
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果（items 为 collectionId）
     */
    PageResponse<Long> listCollectionIds(long userId, int page, int pageSize);
}

