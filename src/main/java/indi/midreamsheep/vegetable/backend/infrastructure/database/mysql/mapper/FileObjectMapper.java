package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.FileObjectEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件对象 Mapper（MySQL）。
 */
@Mapper
public interface FileObjectMapper extends BaseMapper<FileObjectEntity> {
}

