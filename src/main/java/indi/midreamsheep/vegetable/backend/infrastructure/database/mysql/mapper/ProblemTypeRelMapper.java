package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.ProblemTypeRelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目-题型关联表 Mapper（MySQL）。
 */
@Mapper
public interface ProblemTypeRelMapper extends BaseMapper<ProblemTypeRelEntity> {
}
