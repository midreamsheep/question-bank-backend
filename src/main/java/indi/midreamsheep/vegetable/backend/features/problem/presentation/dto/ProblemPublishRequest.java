package indi.midreamsheep.vegetable.backend.features.problem.presentation.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 题目发布请求：允许在发布时补充学科与标签，并支持新增标签。
 *
 * @param subject 学科（可选；不传则沿用草稿中的 subject）
 * @param tagIds 标签ID列表（可选；不传则沿用草稿中的 tagIds）
 * @param newTags 新标签名称列表（可选；将自动创建并加入最终标签列表）
 */
public record ProblemPublishRequest(
        @Size(max = 64) String subject,
        List<Long> tagIds,
        @Size(max = 20) List<String> newTags
) {
}
