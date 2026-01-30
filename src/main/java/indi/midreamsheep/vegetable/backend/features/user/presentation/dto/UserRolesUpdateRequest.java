package indi.midreamsheep.vegetable.backend.features.user.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 用户角色更新请求 DTO。
 *
 * @param roleIds 角色ID列表
 */
public record UserRolesUpdateRequest(
        @NotNull List<Long> roleIds
) {
}
