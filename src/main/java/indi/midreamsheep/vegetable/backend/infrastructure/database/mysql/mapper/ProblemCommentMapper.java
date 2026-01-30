package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.ProblemCommentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目评论 Mapper（MySQL）。
 */
@Mapper
public interface ProblemCommentMapper extends BaseMapper<ProblemCommentEntity> {
}

