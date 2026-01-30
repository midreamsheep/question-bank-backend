package indi.midreamsheep.vegetable.backend.features.like.domain.port;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;

/**
 * 用户点赞评论仓储端口。
 */
public interface LikeCommentRepositoryPort {

    /**
     * 点赞评论（幂等）。
     *
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 是否发生状态变更（从未点赞/取消点赞 -> 点赞）
     */
    boolean add(long userId, long commentId);

    /**
     * 取消点赞评论（幂等）。
     *
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 是否发生状态变更（点赞 -> 取消点赞）
     */
    boolean remove(long userId, long commentId);

    /**
     * 点赞评论 ID 列表（分页，按点赞时间倒序）。
     *
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果（items 为 commentId）
     */
    PageResponse<Long> listCommentIds(long userId, int page, int pageSize);
}

