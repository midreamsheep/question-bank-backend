package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.UserLikeCommentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户点赞评论 Mapper（MySQL）。
 */
@Mapper
public interface UserLikeCommentMapper extends BaseMapper<UserLikeCommentEntity> {
}

