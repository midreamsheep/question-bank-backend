package indi.midreamsheep.vegetable.backend.features.collection.presentation.dto;

/**
 * 题单条目响应 DTO。
 *
 * @param problemId 题目ID
 * @param sortOrder 排序
 */
public record CollectionItemResponse(
        long problemId,
        int sortOrder
) {
}
