package indi.midreamsheep.vegetable.backend.features.role.presentation.dto;

/**
 * 角色响应 DTO。
 *
 * @param id 角色ID
 * @param code 角色编码
 * @param name 角色名称
 */
public record RoleResponse(
        long id,
        String code,
        String name
) {
}
