package indi.midreamsheep.vegetable.backend.features.report.domain.command;

import indi.midreamsheep.vegetable.backend.features.report.domain.ReportTargetType;

/**
 * 举报创建命令。
 *
 * @param reporterId 举报人用户ID
 * @param targetType 目标类型
 * @param targetId 目标ID
 * @param reason 举报原因
 */
public record ReportCreateCommand(
        long reporterId,
        ReportTargetType targetType,
        long targetId,
        String reason
) {
}

