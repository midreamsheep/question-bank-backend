package indi.midreamsheep.vegetable.backend.features.like.domain.port;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;

/**
 * 用户点赞题目仓储端口。
 */
public interface LikeProblemRepositoryPort {

    /**
     * 点赞（幂等）。
     *
     * @param userId 用户ID
     * @param problemId 题目ID
     * @return 是否发生状态变更（从未点赞/取消点赞 -> 点赞）
     */
    boolean add(long userId, long problemId);

    /**
     * 取消点赞（幂等）。
     *
     * @param userId 用户ID
     * @param problemId 题目ID
     * @return 是否发生状态变更（点赞 -> 取消点赞）
     */
    boolean remove(long userId, long problemId);

    /**
     * 点赞题目 ID 列表（分页，按点赞时间倒序）。
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果（items 为 problemId）
     */
    PageResponse<Long> listProblemIds(long userId, int page, int pageSize);
}

