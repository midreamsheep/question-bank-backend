package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.ProblemCategoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目-分类关联表 Mapper（MySQL）。
 */
@Mapper
public interface ProblemCategoryMapper extends BaseMapper<ProblemCategoryEntity> {
}
