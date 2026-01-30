package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色表 Mapper（MySQL）。
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {
}
