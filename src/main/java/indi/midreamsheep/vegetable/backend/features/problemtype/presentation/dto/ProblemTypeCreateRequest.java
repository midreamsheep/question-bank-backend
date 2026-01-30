package indi.midreamsheep.vegetable.backend.features.problemtype.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 题型创建请求 DTO。
 *
 * @param subject 学科
 * @param name 名称
 * @param description 描述
 * @param sortOrder 排序
 * @param enabled 是否启用
 */
public record ProblemTypeCreateRequest(
        String subject,
        @NotBlank String name,
        String description,
        @Min(0) Integer sortOrder,
        Boolean enabled
) {
}
