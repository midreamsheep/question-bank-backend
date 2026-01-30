package indi.midreamsheep.vegetable.backend.features.problem.domain.model;

import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目列表摘要数据。
 *
 * @param id 题目ID
 * @param title 标题
 * @param subject 学科
 * @param difficulty 难度
 * @param status 状态
 * @param visibility 可见性
 * @param publishedAt 发布时间
 * @param authorId 作者ID
 * @param tagIds 标签ID列表
 */
public record ProblemSummaryData(
        long id,
        String title,
        String subject,
        int difficulty,
        ProblemStatus status,
        Visibility visibility,
        LocalDateTime publishedAt,
        long authorId,
        List<Long> tagIds
) {
}
