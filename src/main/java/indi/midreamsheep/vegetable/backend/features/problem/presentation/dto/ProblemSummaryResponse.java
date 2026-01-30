package indi.midreamsheep.vegetable.backend.features.problem.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目摘要响应 DTO。
 *
 * @param id 题目ID
 * @param title 标题
 * @param subject 学科
 * @param difficulty 难度
 * @param status 状态
 * @param visibility 可见性
 * @param publishedAt 发布时间
 * @param author 作者信息
 * @param tagIds 标签ID列表
 * @param tags 标签对象列表（便于前端直接展示）
 */
public record ProblemSummaryResponse(
        long id,
        String title,
        String subject,
        int difficulty,
        ProblemStatus status,
        Visibility visibility,
        LocalDateTime publishedAt,
        ProblemAuthorResponse author,
        List<Long> tagIds,
        List<ProblemTagResponse> tags
) {
}
