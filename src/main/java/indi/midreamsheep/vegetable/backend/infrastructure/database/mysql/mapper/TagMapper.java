package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.TagEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签表 Mapper（MySQL）。
 */
@Mapper
public interface TagMapper extends BaseMapper<TagEntity> {
}
