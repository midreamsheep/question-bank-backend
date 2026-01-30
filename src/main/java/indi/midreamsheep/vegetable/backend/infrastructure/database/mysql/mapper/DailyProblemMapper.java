package indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.DailyProblemEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 每日一题表 Mapper（MySQL）。
 */
@Mapper
public interface DailyProblemMapper extends BaseMapper<DailyProblemEntity> {
}
