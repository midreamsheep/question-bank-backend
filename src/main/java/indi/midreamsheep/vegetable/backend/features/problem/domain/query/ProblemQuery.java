package indi.midreamsheep.vegetable.backend.features.problem.domain.query;

import java.util.List;

/**
 * 题目列表查询参数。
 *
 * @param subject 学科
 * @param tagIds 标签ID列表（多选，OR 语义）
 * @param difficultyMin 难度下限
 * @param difficultyMax 难度上限
 * @param keyword 关键词（标题）
 * @param sort 排序字段（LATEST/PUBLISHED_AT/DIFFICULTY）
 * @param page 页码（从 1 开始）
 * @param pageSize 每页大小
 */
public record ProblemQuery(
        String subject,
        List<Long> tagIds,
        Integer difficultyMin,
        Integer difficultyMax,
        String keyword,
        String sort,
        int page,
        int pageSize
) {
}
