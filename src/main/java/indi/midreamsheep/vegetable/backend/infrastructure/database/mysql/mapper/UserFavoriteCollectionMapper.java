package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.UserFavoriteCollectionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收藏题单 Mapper（MySQL）。
 */
@Mapper
public interface UserFavoriteCollectionMapper extends BaseMapper<UserFavoriteCollectionEntity> {
}

