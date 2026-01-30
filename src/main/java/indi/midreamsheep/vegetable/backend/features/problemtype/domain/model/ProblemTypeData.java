package indi.midreamsheep.vegetable.backend.features.problemtype.domain.model;

/**
 * 题型数据模型。
 *
 * @param id 题型ID
 * @param subject 学科
 * @param name 名称
 * @param description 描述
 * @param sortOrder 排序
 * @param enabled 是否启用
 */
public record ProblemTypeData(
        long id,
        String subject,
        String name,
        String description,
        int sortOrder,
        boolean enabled
) {
}
