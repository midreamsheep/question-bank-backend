package indi.midreamsheep.vegetable.backend.features.favorite.domain.port;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;

/**
 * 用户收藏题目仓储端口。
 */
public interface FavoriteProblemRepositoryPort {

    /**
     * 收藏题目（幂等）。
     *
     * @param userId 用户ID
     * @param problemId 题目ID
     * @return 是否发生状态变更（从未收藏/取消收藏 -> 收藏）
     */
    boolean add(long userId, long problemId);

    /**
     * 取消收藏（幂等）。
     *
     * @param userId 用户ID
     * @param problemId 题目ID
     * @return 是否发生状态变更（收藏 -> 取消收藏）
     */
    boolean remove(long userId, long problemId);

    /**
     * 收藏题目 ID 列表（分页，按收藏时间倒序）。
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果（items 为 problemId）
     */
    PageResponse<Long> listProblemIds(long userId, int page, int pageSize);
}

