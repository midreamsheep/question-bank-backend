package indi.midreamsheep.vegetable.backend.features.comment.presentation.dto;

import java.time.LocalDateTime;

/**
 * 题目评论响应 DTO。
 *
 * @param id 评论ID
 * @param problemId 题目ID
 * @param userId 评论用户ID
 * @param parentId 父评论ID
 * @param replyToCommentId 回复的评论ID
 * @param content 内容
 * @param likeCount 点赞数
 * @param deleted 是否已删除
 * @param createdAt 创建时间
 */
public record ProblemCommentResponse(
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
