package indi.midreamsheep.vegetable.backend.features.tag.domain.command;

/**
 * 标签创建命令。
 *
 * @param subject 学科
 * @param name 名称
 */
public record TagCreateCommand(
        String subject,
        String name
) {
}
