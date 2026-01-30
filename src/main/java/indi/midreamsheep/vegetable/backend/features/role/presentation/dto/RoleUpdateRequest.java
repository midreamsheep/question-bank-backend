package indi.midreamsheep.vegetable.backend.features.role.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 角色更新请求 DTO。
 *
 * @param name 角色名称
 */
public record RoleUpdateRequest(
        @NotBlank @Size(max = 64) String name
) {
}
