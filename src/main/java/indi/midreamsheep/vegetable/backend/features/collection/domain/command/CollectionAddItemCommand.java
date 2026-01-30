package indi.midreamsheep.vegetable.backend.features.collection.domain.command;

/**
 * 题单添加条目命令。
 *
 * @param collectionId 题单ID
 * @param authorId 作者ID
 * @param problemId 题目ID
 * @param sortOrder 排序
 */
public record CollectionAddItemCommand(
        long collectionId,
        long authorId,
        long problemId,
        int sortOrder
) {
}
