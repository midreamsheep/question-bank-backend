package indi.midreamsheep.vegetable.backend.features.user.domain.command;

import indi.midreamsheep.vegetable.backend.features.user.domain.UserStatus;

/**
 * 用户状态更新命令。
 *
 * @param id 用户ID
 * @param status 状态
 */
public record UserStatusUpdateCommand(
        long id,
        UserStatus status
) {
}
