package indi.midreamsheep.vegetable.backend.features.collection.domain.command;

import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;

/**
 * 题单创建命令。
 *
 * @param authorId 作者ID
 * @param name 名称
 * @param description 简介
 * @param visibility 可见性
 * @param shareKey 分享 key
 */
public record CollectionCreateCommand(
        long authorId,
        String name,
        String description,
        Visibility visibility,
        String shareKey
) {
}
