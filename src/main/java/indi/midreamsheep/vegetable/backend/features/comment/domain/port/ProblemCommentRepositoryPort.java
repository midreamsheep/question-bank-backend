package indi.midreamsheep.vegetable.backend.features.comment.domain.port;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.comment.domain.model.ProblemCommentData;

import java.util.Optional;

/**
 * 题目评论仓储端口。
 */
public interface ProblemCommentRepositoryPort {

    /**
     * 创建评论并返回评论ID。
     *
     * @param problemId 题目ID
     * @param userId 评论用户ID
     * @param parentId 父评论ID（可为空）
     * @param replyToCommentId 回复的评论ID（可为空）
     * @param content 内容
     * @return 评论ID
     */
    long create(long problemId, long userId, Long parentId, Long replyToCommentId, String content);

    /**
     * 评论列表（分页）。
     *
     * @param problemId 题目ID
     * @param parentId 父评论ID（为空表示顶层评论）
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResponse<ProblemCommentData> listByProblem(long problemId, Long parentId, int page, int pageSize);

    /**
     * 根据 ID 获取评论（仅未删除）。
     *
     * @param id 评论ID
     * @return 评论（可为空）
     */
    Optional<ProblemCommentData> findById(long id);

    /**
     * 根据 ID 获取评论（包含已删除）。
     *
     * @param id 评论ID
     * @return 评论（可为空）
     */
    Optional<ProblemCommentData> findByIdIncludingDeleted(long id);

    /**
     * 软删除评论。
     *
     * @param id 评论ID
     */
    void softDelete(long id);

    /**
     * 评论点赞数 +1。
     *
     * @param id 评论ID
     */
    void incrementLikeCount(long id);

    /**
     * 评论点赞数 -1（不会减到负数）。
     *
     * @param id 评论ID
     */
    void decrementLikeCount(long id);
}
