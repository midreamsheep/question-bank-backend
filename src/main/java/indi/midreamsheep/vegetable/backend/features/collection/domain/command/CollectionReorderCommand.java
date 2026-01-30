package indi.midreamsheep.vegetable.backend.features.collection.domain.command;

import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionItemData;

import java.util.List;

/**
 * 题单调整顺序命令。
 *
 * @param collectionId 题单ID
 * @param authorId 作者ID
 * @param items 排序项
 */
public record CollectionReorderCommand(
        long collectionId,
        long authorId,
        List<CollectionItemData> items
) {
}
