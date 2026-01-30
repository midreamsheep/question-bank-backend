package indi.midreamsheep.vegetable.backend.features.tag.presentation.dto;

/**
 * 标签响应 DTO。
 *
 * @param id 标签ID
 * @param subject 学科
 * @param name 名称
 */
public record TagResponse(
        long id,
        String subject,
        String name
) {
}
