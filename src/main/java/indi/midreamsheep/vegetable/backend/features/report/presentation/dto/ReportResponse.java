package indi.midreamsheep.vegetable.backend.features.report.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.report.domain.ReportStatus;
import indi.midreamsheep.vegetable.backend.features.report.domain.ReportTargetType;

import java.time.LocalDateTime;

/**
 * 举报响应 DTO。
 *
 * @param id 举报ID
 * @param reporterId 举报人用户ID
 * @param targetType 目标类型
 * @param targetId 目标ID
 * @param reason 举报原因
 * @param status 状态
 * @param handlerId 处理人用户ID
 * @param handledAt 处理时间
 * @param handlingNote 处理备注
 * @param createdAt 创建时间
 */
public record ReportResponse(
        long id,
        long reporterId,
        ReportTargetType targetType,
        long targetId,
        String reason,
        ReportStatus status,
        Long handlerId,
        LocalDateTime handledAt,
        String handlingNote,
        LocalDateTime createdAt
) {
}

