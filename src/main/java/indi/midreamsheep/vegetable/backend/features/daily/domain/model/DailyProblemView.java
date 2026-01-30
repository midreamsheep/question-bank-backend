package indi.midreamsheep.vegetable.backend.features.daily.domain.model;

import java.time.LocalDate;

/**
 * 每日一题展示数据。
 *
 * @param id 主键
 * @param day 日期
 * @param copywriting 文案
 * @param problem 题目摘要
 */
public record DailyProblemView(
        long id,
        LocalDate day,
        String copywriting,
        DailyProblemProblemSummary problem
) {
}
