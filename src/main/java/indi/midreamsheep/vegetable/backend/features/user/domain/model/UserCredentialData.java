package indi.midreamsheep.vegetable.backend.features.user.domain.model;

import indi.midreamsheep.vegetable.backend.features.user.domain.UserStatus;

/**
 * 用户凭证数据。
 *
 * @param id 用户ID
 * @param username 用户名
 * @param passwordHash 密码哈希
 * @param status 状态
 */
public record UserCredentialData(
        long id,
        String username,
        String passwordHash,
        UserStatus status
) {
}
