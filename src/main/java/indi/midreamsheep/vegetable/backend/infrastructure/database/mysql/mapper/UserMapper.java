package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper（MySQL）。
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
