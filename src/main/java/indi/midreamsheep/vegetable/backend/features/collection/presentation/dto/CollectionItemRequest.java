package indi.midreamsheep.vegetable.backend.features.collection.presentation.dto;

import jakarta.validation.constraints.Min;

/**
 * 题单条目请求 DTO。
 *
 * @param problemId 题目ID
 * @param sortOrder 排序
 */
public record CollectionItemRequest(
        @Min(1) long problemId,
        @Min(0) int sortOrder
) {
}
