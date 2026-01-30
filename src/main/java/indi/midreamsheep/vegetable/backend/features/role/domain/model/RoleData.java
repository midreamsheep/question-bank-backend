package indi.midreamsheep.vegetable.backend.features.role.domain.model;

/**
 * 角色数据。
 *
 * @param id 角色ID
 * @param code 角色编码
 * @param name 角色名称
 */
public record RoleData(
        long id,
        String code,
        String name
) {
}
