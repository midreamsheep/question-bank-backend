package indi.midreamsheep.vegetable.backend.features.collection.domain.model;

/**
 * 题单条目数据。
 *
 * @param problemId 题目ID
 * @param sortOrder 排序
 */
public record CollectionItemData(
        long problemId,
        int sortOrder
) {
}
