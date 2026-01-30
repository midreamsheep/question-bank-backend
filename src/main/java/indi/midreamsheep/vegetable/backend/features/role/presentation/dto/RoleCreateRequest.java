package indi.midreamsheep.vegetable.backend.features.role.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 角色创建请求 DTO。
 *
 * @param code 角色编码
 * @param name 角色名称
 */
public record RoleCreateRequest(
        @NotBlank @Size(max = 32) String code,
        @NotBlank @Size(max = 64) String name
) {
}
