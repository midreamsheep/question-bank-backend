package indi.midreamsheep.vegetable.backend.features.problemtype.presentation.dto;

/**
 * 题型响应 DTO。
 *
 * @param id 题型ID
 * @param subject 学科
 * @param name 名称
 * @param description 描述
 * @param sortOrder 排序
 * @param enabled 是否启用
 */
public record ProblemTypeResponse(
        long id,
        String subject,
        String name,
        String description,
        int sortOrder,
        boolean enabled
) {
}
