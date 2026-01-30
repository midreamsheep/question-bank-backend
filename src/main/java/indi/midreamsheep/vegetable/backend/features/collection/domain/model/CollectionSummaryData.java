package indi.midreamsheep.vegetable.backend.features.collection.domain.model;

import indi.midreamsheep.vegetable.backend.features.collection.domain.CollectionStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;

/**
 * 题单摘要数据。
 *
 * @param id 题单ID
 * @param name 名称
 * @param description 简介
 * @param visibility 可见性
 * @param status 状态
 * @param itemCount 题目数量
 * @param authorId 作者ID
 */
public record CollectionSummaryData(
        long id,
        String name,
        String description,
        Visibility visibility,
        CollectionStatus status,
        int itemCount,
        long authorId
) {
}
