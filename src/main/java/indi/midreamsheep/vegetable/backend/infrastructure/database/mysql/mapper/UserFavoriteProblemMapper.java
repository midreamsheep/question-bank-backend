package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.UserFavoriteProblemEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收藏题目 Mapper（MySQL）。
 */
@Mapper
public interface UserFavoriteProblemMapper extends BaseMapper<UserFavoriteProblemEntity> {
}

