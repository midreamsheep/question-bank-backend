package indi.midreamsheep.vegetable.backend.features.user.domain.query;

import indi.midreamsheep.vegetable.backend.features.user.domain.UserStatus;

/**
 * 用户查询条件（管理端）。
 *
 * @param keyword 关键字（用户名/昵称）
 * @param status 状态
 * @param page 页码
 * @param pageSize 每页大小
 */
public record UserQuery(
        String keyword,
        UserStatus status,
        int page,
        int pageSize
) {
}
