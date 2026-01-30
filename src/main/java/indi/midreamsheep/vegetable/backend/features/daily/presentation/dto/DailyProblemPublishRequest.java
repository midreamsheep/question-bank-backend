package indi.midreamsheep.vegetable.backend.features.daily.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * 每日一题发布请求 DTO。
 *
 * @param day 日期
 * @param problemId 题目ID
 * @param copywriting 文案
 */
public record DailyProblemPublishRequest(
        @NotNull LocalDate day,
        @Min(1) long problemId,
        String copywriting
) {
}
