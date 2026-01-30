package indi.midreamsheep.vegetable.backend.features.tag.domain.model;

/**
 * 标签数据模型。
 *
 * @param id 标签ID
 * @param subject 学科
 * @param name 名称
 */
public record TagData(
        long id,
        String subject,
        String name
) {
}
