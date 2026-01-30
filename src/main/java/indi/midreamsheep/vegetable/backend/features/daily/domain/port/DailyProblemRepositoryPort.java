package indi.midreamsheep.vegetable.backend.features.daily.domain.port;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.daily.domain.model.DailyProblemData;

import java.time.LocalDate;
import java.util.List;

/**
 * 每日一题仓储端口。
 */
public interface DailyProblemRepositoryPort {

    /**
     * 根据日期获取每日一题列表（包含已撤回数据，由调用方决定如何过滤）。
     *
     * @param day 日期
     * @return 每日一题列表
     */
    List<DailyProblemData> findByDay(LocalDate day);

    /**
     * 按日期范围查询每日一题。
     *
     * @param from 起始日期
     * @param to 截止日期
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResponse<DailyProblemData> list(LocalDate from, LocalDate to, int page, int pageSize);

    /**
     * 发布每日一题（同一天同一题目重复发布时，按“替换/重发”处理）。
     *
     * @param data 每日一题数据
     * @return 发布后的每日一题
     */
    DailyProblemData publish(DailyProblemData data);

    /**
     * 撤回每日一题（按日期撤回当天所有记录）。
     *
     * @param day 日期
     * @param operatorId 操作人
     * @return 撤回后的每日一题列表
     */
    List<DailyProblemData> revoke(LocalDate day, long operatorId);

    /**
     * 按 ID 撤回每日一题。
     *
     * @param id ID
     * @param operatorId 操作人
     * @return 撤回后的每日一题
     */
    DailyProblemData revokeById(long id, long operatorId);
}
