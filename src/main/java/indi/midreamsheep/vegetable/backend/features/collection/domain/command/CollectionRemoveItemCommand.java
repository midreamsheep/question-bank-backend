package indi.midreamsheep.vegetable.backend.features.collection.domain.command;

/**
 * 从题单移除题目命令。
 *
 * @param collectionId 题单ID
 * @param authorId 作者ID
 * @param problemId 题目ID
 */
public record CollectionRemoveItemCommand(
        long collectionId,
        long authorId,
        long problemId
) {
}

