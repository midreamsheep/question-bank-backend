package indi.midreamsheep.vegetable.backend.features.problemtype.domain.command;

/**
 * 题型更新命令。
 *
 * @param id 题型ID
 * @param subject 学科
 * @param name 名称
 * @param description 描述
 * @param sortOrder 排序
 * @param enabled 是否启用
 */
public record ProblemTypeUpdateCommand(
        long id,
        String subject,
        String name,
        String description,
        int sortOrder,
        boolean enabled
) {
}
