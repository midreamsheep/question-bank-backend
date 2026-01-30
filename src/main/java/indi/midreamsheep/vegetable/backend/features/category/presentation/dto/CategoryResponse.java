package indi.midreamsheep.vegetable.backend.features.category.presentation.dto;

/**
 * 分类响应 DTO。
 *
 * @param id 分类ID
 * @param subject 学科
 * @param parentId 父分类ID
 * @param name 名称
 * @param description 描述
 * @param sortOrder 排序
 * @param enabled 是否启用
 */
public record CategoryResponse(
        long id,
        String subject,
        Long parentId,
        String name,
        String description,
        int sortOrder,
        boolean enabled
) {
}
