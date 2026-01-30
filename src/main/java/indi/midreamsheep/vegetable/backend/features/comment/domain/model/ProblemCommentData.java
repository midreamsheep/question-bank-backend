package indi.midreamsheep.vegetable.backend.features.comment.domain.model;

import java.time.LocalDateTime;

/**
 * 题目评论数据。
 *
 * @param id 评论ID
 * @param problemId 题目ID
 * @param userId 评论用户ID
 * @param parentId 父评论ID（楼中楼）
 * @param replyToCommentId 回复的评论ID（可选）
 * @param content 内容（删除后可为 null）
 * @param likeCount 点赞数
 * @param deleted 是否已删除
 * @param createdAt 创建时间
 */
public record ProblemCommentData(
        long id,
        long problemId,
        long userId,
        Long parentId,
        Long replyToCommentId,
        String content,
        long likeCount,
        boolean deleted,
        LocalDateTime createdAt
) {
}
