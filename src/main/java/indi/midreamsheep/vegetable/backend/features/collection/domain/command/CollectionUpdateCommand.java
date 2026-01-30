package indi.midreamsheep.vegetable.backend.features.collection.domain.command;

import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;

/**
 * 题单更新命令。
 *
 * @param id 题单ID
 * @param authorId 作者ID
 * @param name 名称
 * @param description 描述
 * @param visibility 可见性
 * @param shareKey 分享 key（UNLISTED 需要）
 */
public record CollectionUpdateCommand(
        long id,
        long authorId,
        String name,
        String description,
        Visibility visibility,
        String shareKey
) {
}

