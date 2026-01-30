package indi.midreamsheep.vegetable.backend.features.collection.domain.model;

import indi.midreamsheep.vegetable.backend.features.collection.domain.CollectionStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;

import java.util.List;

/**
 * 题单详情数据。
 *
 * @param id 题单ID
 * @param authorId 作者ID
 * @param name 名称
 * @param description 简介
 * @param visibility 可见性
 * @param shareKey 分享 key
 * @param status 状态
 * @param items 题单条目
 */
public record CollectionDetailData(
        long id,
        long authorId,
        String name,
        String description,
        Visibility visibility,
        String shareKey,
        CollectionStatus status,
        List<CollectionItemData> items
) {
}
