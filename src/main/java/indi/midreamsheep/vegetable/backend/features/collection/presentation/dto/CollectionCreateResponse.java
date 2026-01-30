package indi.midreamsheep.vegetable.backend.features.collection.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.collection.domain.CollectionStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;

/**
 * 题单创建响应 DTO。
 *
 * @param id 题单ID
 * @param status 状态
 * @param visibility 可见性
 * @param shareKey 分享 key
 */
public record CollectionCreateResponse(
        long id,
        CollectionStatus status,
        Visibility visibility,
        String shareKey
) {
}
