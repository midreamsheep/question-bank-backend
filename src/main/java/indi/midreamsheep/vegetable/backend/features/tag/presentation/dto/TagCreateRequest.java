package indi.midreamsheep.vegetable.backend.features.tag.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 标签创建请求 DTO。
 *
 * @param subject 学科
 * @param name 名称
 */
public record TagCreateRequest(
        @NotBlank @Size(max = 64) String subject,
        @NotBlank String name
) {
}
