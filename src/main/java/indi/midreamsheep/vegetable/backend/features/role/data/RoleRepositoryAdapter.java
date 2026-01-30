package indi.midreamsheep.vegetable.backend.features.role.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import indi.midreamsheep.vegetable.backend.features.role.domain.command.RoleCreateCommand;
import indi.midreamsheep.vegetable.backend.features.role.domain.command.RoleUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.role.domain.model.RoleData;
import indi.midreamsheep.vegetable.backend.features.role.domain.port.RoleRepositoryPort;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.RoleEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.UserRoleEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.RoleMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.UserRoleMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 角色仓储适配器：通过 MyBatis-Plus 完成持久化。
 */
@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    /**
     * 构造角色仓储适配器。
     *
     * @param roleMapper 角色 Mapper
     * @param userRoleMapper 用户角色 Mapper
     */
    public RoleRepositoryAdapter(RoleMapper roleMapper, UserRoleMapper userRoleMapper) {
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public List<RoleData> list() {
        QueryWrapper<RoleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0)
                .orderByAsc("id");
        return roleMapper.selectList(wrapper).stream()
                .map(RoleRepositoryAdapter::toData)
                .toList();
    }

    @Override
    public Optional<RoleData> findById(long id) {
        QueryWrapper<RoleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0);
        return Optional.ofNullable(roleMapper.selectOne(wrapper))
                .map(RoleRepositoryAdapter::toData);
    }

    @Override
    public Optional<RoleData> findByCode(String code) {
        if (!StringUtils.hasText(code)) {
            return Optional.empty();
        }
        QueryWrapper<RoleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code)
                .eq("deleted", 0);
        return Optional.ofNullable(roleMapper.selectOne(wrapper))
                .map(RoleRepositoryAdapter::toData);
    }

    @Override
    public long create(RoleCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        RoleEntity entity = new RoleEntity();
        entity.setCode(command.code());
        entity.setName(command.name());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setDeleted(0);
        roleMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public RoleData update(RoleUpdateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<RoleEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", command.id())
                .set("name", command.name())
                .set("updated_at", now);
        roleMapper.update(null, wrapper);
        return findById(command.id()).orElseThrow(() -> new IllegalStateException("角色更新失败"));
    }

    @Override
    public void softDelete(long id) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<RoleEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .set("deleted", 1)
                .set("updated_at", now);
        roleMapper.update(null, wrapper);
    }

    @Override
    public List<RoleData> listByUserId(long userId) {
        List<Long> roleIds = listUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        QueryWrapper<RoleEntity> wrapper = new QueryWrapper<>();
        wrapper.in("id", roleIds)
                .eq("deleted", 0);
        return roleMapper.selectList(wrapper).stream()
                .map(RoleRepositoryAdapter::toData)
                .toList();
    }

    @Override
    public void replaceUserRoles(long userId, List<Long> roleIds) {
        LocalDateTime now = LocalDateTime.now();
        softDeleteUserRoles(userId, now);
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        for (Long roleId : roleIds) {
            UserRoleEntity entity = new UserRoleEntity();
            entity.setUserId(userId);
            entity.setRoleId(roleId);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            entity.setDeleted(0);
            userRoleMapper.insert(entity);
        }
    }

    @Override
    public boolean hasRole(long userId, String roleCode) {
        Optional<RoleData> role = findByCode(roleCode);
        if (role.isEmpty()) {
            return false;
        }
        QueryWrapper<UserRoleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("role_id", role.get().id())
                .eq("deleted", 0);
        return userRoleMapper.selectCount(wrapper) > 0;
    }

    /**
     * 获取用户角色ID列表。
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    private List<Long> listUserRoleIds(long userId) {
        QueryWrapper<UserRoleEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .eq("deleted", 0);
        return userRoleMapper.selectList(wrapper).stream()
                .map(UserRoleEntity::getRoleId)
                .toList();
    }

    /**
     * 软删除用户角色。
     *
     * @param userId 用户ID
     * @param now 当前时间
     */
    private void softDeleteUserRoles(long userId, LocalDateTime now) {
        UpdateWrapper<UserRoleEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id", userId)
                .set("deleted", 1)
                .set("updated_at", now);
        userRoleMapper.update(null, wrapper);
    }

    /**
     * 转换为角色数据。
     *
     * @param entity 角色实体
     * @return 角色数据
     */
    private static RoleData toData(RoleEntity entity) {
        return new RoleData(
                entity.getId(),
                entity.getCode(),
                entity.getName()
        );
    }
}
