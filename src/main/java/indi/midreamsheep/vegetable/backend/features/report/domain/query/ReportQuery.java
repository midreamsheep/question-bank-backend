package indi.midreamsheep.vegetable.backend.features.report.domain.query;

import indi.midreamsheep.vegetable.backend.features.report.domain.ReportStatus;
import indi.midreamsheep.vegetable.backend.features.report.domain.ReportTargetType;

/**
 * 举报查询条件。
 *
 * @param targetType 目标类型（可为空）
 * @param status 状态（可为空）
 * @param page 页码
 * @param pageSize 每页大小
 */
public record ReportQuery(
        ReportTargetType targetType,
        ReportStatus status,
        int page,
        int pageSize
) {
}

