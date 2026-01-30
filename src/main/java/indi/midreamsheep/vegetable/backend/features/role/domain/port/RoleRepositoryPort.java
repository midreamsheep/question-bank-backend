package indi.midreamsheep.vegetable.backend.features.role.domain.port;

import indi.midreamsheep.vegetable.backend.features.role.domain.command.RoleCreateCommand;
import indi.midreamsheep.vegetable.backend.features.role.domain.command.RoleUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.role.domain.model.RoleData;

import java.util.List;
import java.util.Optional;

/**
 * 角色仓储端口。
 */
public interface RoleRepositoryPort {

    /**
     * 获取角色列表。
     *
     * @return 角色列表
     */
    List<RoleData> list();

    /**
     * 根据 ID 获取角色。
     *
     * @param id 角色ID
     * @return 角色数据
     */
    Optional<RoleData> findById(long id);

    /**
     * 根据编码获取角色。
     *
     * @param code 角色编码
     * @return 角色数据
     */
    Optional<RoleData> findByCode(String code);

    /**
     * 创建角色。
     *
     * @param command 创建命令
     * @return 角色ID
     */
    long create(RoleCreateCommand command);

    /**
     * 更新角色。
     *
     * @param command 更新命令
     * @return 更新后的角色
     */
    RoleData update(RoleUpdateCommand command);

    /**
     * 删除角色（软删除）。
     *
     * @param id 角色ID
     */
    void softDelete(long id);

    /**
     * 获取用户的角色列表。
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<RoleData> listByUserId(long userId);

    /**
     * 替换用户角色。
     *
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void replaceUserRoles(long userId, List<Long> roleIds);

    /**
     * 判断用户是否拥有角色。
     *
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否拥有角色
     */
    boolean hasRole(long userId, String roleCode);
}
