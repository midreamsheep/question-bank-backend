package indi.midreamsheep.vegetable.backend.features.problemtype.domain.command;

/**
 * 题型创建命令。
 *
 * @param subject 学科
 * @param name 名称
 * @param description 描述
 * @param sortOrder 排序
 * @param enabled 是否启用
 */
public record ProblemTypeCreateCommand(
        String subject,
        String name,
        String description,
        int sortOrder,
        boolean enabled
) {
}
