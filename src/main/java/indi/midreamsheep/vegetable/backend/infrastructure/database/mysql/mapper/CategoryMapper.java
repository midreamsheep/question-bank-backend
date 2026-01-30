package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.CategoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类表 Mapper（MySQL）。
 */
@Mapper
public interface CategoryMapper extends BaseMapper<CategoryEntity> {
}
