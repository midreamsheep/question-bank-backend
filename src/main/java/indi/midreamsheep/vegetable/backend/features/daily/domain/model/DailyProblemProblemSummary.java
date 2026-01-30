package indi.midreamsheep.vegetable.backend.features.daily.domain.model;

/**
 * 每日一题引用的题目摘要。
 *
 * @param id 题目ID
 * @param title 标题
 * @param subject 学科
 * @param difficulty 难度
 */
public record DailyProblemProblemSummary(
        long id,
        String title,
        String subject,
        int difficulty
) {
}
