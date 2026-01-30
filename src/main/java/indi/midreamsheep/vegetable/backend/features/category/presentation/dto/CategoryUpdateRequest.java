package indi.midreamsheep.vegetable.backend.features.category.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 分类更新请求 DTO。
 *
 * @param subject 学科
 * @param parentId 父分类ID
 * @param name 名称
 * @param description 描述
 * @param sortOrder 排序
 * @param enabled 是否启用
 */
public record CategoryUpdateRequest(
        String subject,
        Long parentId,
        @NotBlank String name,
        String description,
        @Min(0) Integer sortOrder,
        Boolean enabled
) {
}
