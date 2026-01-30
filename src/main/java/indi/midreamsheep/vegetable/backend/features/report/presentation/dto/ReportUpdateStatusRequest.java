package indi.midreamsheep.vegetable.backend.features.report.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.report.domain.ReportStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 举报状态更新请求 DTO（管理端）。
 *
 * @param status 新状态：RESOLVED/REJECTED
 * @param note 处理备注
 */
public record ReportUpdateStatusRequest(
        @NotNull ReportStatus status,
        @Size(max = 1024) String note
) {
}

