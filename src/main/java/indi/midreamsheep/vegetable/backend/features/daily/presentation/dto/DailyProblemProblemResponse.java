package indi.midreamsheep.vegetable.backend.features.daily.presentation.dto;

/**
 * 每日一题题目摘要响应 DTO。
 *
 * @param id 题目ID
 * @param title 标题
 * @param subject 学科
 * @param difficulty 难度
 */
public record DailyProblemProblemResponse(
        long id,
        String title,
        String subject,
        int difficulty
) {
}
