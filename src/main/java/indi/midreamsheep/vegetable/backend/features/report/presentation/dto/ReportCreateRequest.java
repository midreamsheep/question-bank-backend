package indi.midreamsheep.vegetable.backend.features.report.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.report.domain.ReportTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 举报创建请求 DTO。
 *
 * @param targetType 目标类型
 * @param targetId 目标ID
 * @param reason 举报原因
 */
public record ReportCreateRequest(
        @NotNull ReportTargetType targetType,
        @NotNull Long targetId,
        @NotBlank @Size(max = 1024) String reason
) {
}
