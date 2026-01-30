package indi.midreamsheep.vegetable.backend.features.role.domain.command;

/**
 * 角色更新命令。
 *
 * @param id 角色ID
 * @param name 角色名称
 */
public record RoleUpdateCommand(
        long id,
        String name
) {
}
