package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.CollectionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题单表 Mapper（MySQL）。
 */
@Mapper
public interface CollectionMapper extends BaseMapper<CollectionEntity> {
}
