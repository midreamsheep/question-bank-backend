package indi.midreamsheep.vegetable.backend.features.report.domain.port;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.report.domain.ReportStatus;
import indi.midreamsheep.vegetable.backend.features.report.domain.command.ReportCreateCommand;
import indi.midreamsheep.vegetable.backend.features.report.domain.model.ReportData;
import indi.midreamsheep.vegetable.backend.features.report.domain.query.ReportQuery;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 举报仓储端口。
 */
public interface ReportRepositoryPort {

    /**
     * 创建举报。
     *
     * @param command 创建命令
     * @return 举报ID
     */
    long create(ReportCreateCommand command);

    /**
     * 举报列表（分页）。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    PageResponse<ReportData> list(ReportQuery query);

    /**
     * 根据 ID 查询举报。
     *
     * @param id 举报ID
     * @return 举报（可为空）
     */
    Optional<ReportData> findById(long id);

    /**
     * 更新举报状态。
     *
     * @param id 举报ID
     * @param status 状态
     * @param handlerId 处理人用户ID
     * @param handledAt 处理时间
     * @param handlingNote 处理备注
     */
    void updateStatus(long id, ReportStatus status, long handlerId, LocalDateTime handledAt, String handlingNote);
}

