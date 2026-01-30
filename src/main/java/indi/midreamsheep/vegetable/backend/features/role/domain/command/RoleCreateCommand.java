package indi.midreamsheep.vegetable.backend.features.role.domain.command;

/**
 * 角色创建命令。
 *
 * @param code 角色编码
 * @param name 角色名称
 */
public record RoleCreateCommand(
        String code,
        String name
) {
}
