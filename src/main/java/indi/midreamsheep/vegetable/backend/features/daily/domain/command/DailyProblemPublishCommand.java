package indi.midreamsheep.vegetable.backend.features.daily.domain.command;

import java.time.LocalDate;

/**
 * 每日一题发布命令。
 *
 * @param day 日期
 * @param problemId 题目ID
 * @param copywriting 文案
 * @param operatorId 操作人
 */
public record DailyProblemPublishCommand(
        LocalDate day,
        long problemId,
        String copywriting,
        long operatorId
) {
}
