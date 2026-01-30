package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.UserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联 Mapper（MySQL）。
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {
}
