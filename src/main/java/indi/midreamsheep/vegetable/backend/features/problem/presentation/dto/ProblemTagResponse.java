package indi.midreamsheep.vegetable.backend.features.problem.presentation.dto;

/**
 * 题目标签简要 DTO。
 *
 * @param id 标签ID
 * @param name 标签名称
 */
public record ProblemTagResponse(
        long id,
        String name
) {
}

