package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.ProblemTagEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目-标签关联表 Mapper（MySQL）。
 */
@Mapper
public interface ProblemTagMapper extends BaseMapper<ProblemTagEntity> {
}
