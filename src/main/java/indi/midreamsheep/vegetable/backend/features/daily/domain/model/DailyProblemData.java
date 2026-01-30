package indi.midreamsheep.vegetable.backend.features.daily.domain.model;

import indi.midreamsheep.vegetable.backend.features.daily.domain.DailyProblemStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日一题数据。
 *
 * @param id 主键
 * @param day 日期
 * @param problemId 题目ID
 * @param status 状态
 * @param copywriting 文案
 * @param operatorId 操作人
 * @param publishedAt 发布时间
 * @param revokedAt 撤回时间
 */
public record DailyProblemData(
        long id,
        LocalDate day,
        long problemId,
        DailyProblemStatus status,
        String copywriting,
        long operatorId,
        LocalDateTime publishedAt,
        LocalDateTime revokedAt
) {
}
