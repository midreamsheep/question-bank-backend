package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.ReportEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 举报 Mapper（MySQL）。
 */
@Mapper
public interface ReportMapper extends BaseMapper<ReportEntity> {
}

