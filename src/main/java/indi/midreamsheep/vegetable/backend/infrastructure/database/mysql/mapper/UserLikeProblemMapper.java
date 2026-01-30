package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.UserLikeProblemEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户点赞题目 Mapper（MySQL）。
 */
@Mapper
public interface UserLikeProblemMapper extends BaseMapper<UserLikeProblemEntity> {
}

