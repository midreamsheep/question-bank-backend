package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.ProblemEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目表 Mapper（MySQL）。
 */
@Mapper
public interface ProblemMapper extends BaseMapper<ProblemEntity> {
}

