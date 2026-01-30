package indi.midreamsheep.vegetable.backend.features.daily.presentation.dto;

import java.time.LocalDate;

/**
 * 每日一题响应 DTO。
 *
 * @param id 主键
 * @param day 日期
 * @param copywriting 文案
 * @param problem 题目摘要
 */
public record DailyProblemResponse(
        long id,
        LocalDate day,
        String copywriting,
        DailyProblemProblemResponse problem
) {
}
