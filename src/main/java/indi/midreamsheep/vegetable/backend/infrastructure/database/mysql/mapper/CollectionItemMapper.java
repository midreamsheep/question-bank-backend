package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.CollectionItemEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题单条目表 Mapper（MySQL）。
 */
@Mapper
public interface CollectionItemMapper extends BaseMapper<CollectionItemEntity> {
}
