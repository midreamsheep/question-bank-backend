package indi.midreamsheep.vegetable.backend.features.problem.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.problem.domain.ContentFormat;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 题目更新请求 DTO。
 *
 * @param title 标题
 * @param subject 学科
 * @param difficulty 难度（1-5）
 * @param statementFormat 题干格式
 * @param statement 题干内容
 * @param solutionFormat 解答格式
 * @param solution 解答内容
 * @param visibility 可见性
 * @param tagIds 标签ID列表
 */
public record ProblemUpdateRequest(
        @NotBlank String title,
        @NotBlank @Size(max = 64) String subject,
        @Min(1) @Max(5) int difficulty,
        @NotNull ContentFormat statementFormat,
        @NotBlank String statement,
        ContentFormat solutionFormat,
        String solution,
        @NotNull Visibility visibility,
        List<Long> tagIds
) {
}
