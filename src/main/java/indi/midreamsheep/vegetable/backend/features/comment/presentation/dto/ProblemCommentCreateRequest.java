package indi.midreamsheep.vegetable.backend.features.comment.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 题目评论创建请求 DTO。
 *
 * @param parentId 父评论ID（楼中楼，可为空）
 * @param replyToCommentId 回复的评论ID（可为空；为空时默认等于 parentId）
 * @param content 评论内容
 */
public record ProblemCommentCreateRequest(
        Long parentId,
        Long replyToCommentId,
        @NotBlank @Size(max = 2000) String content
) {
}
