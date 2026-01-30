package indi.midreamsheep.vegetable.backend.features.collection.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.collection.domain.CollectionStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;

import java.util.List;

/**
 * 题单详情响应 DTO。
 *
 * @param id 题单ID
 * @param name 名称
 * @param description 简介
 * @param visibility 可见性
 * @param shareKey 分享 key
 * @param status 状态
 * @param items 题单条目
 */
public record CollectionDetailResponse(
        long id,
        String name,
        String description,
        Visibility visibility,
        String shareKey,
        CollectionStatus status,
        List<CollectionItemResponse> items
) {
}
