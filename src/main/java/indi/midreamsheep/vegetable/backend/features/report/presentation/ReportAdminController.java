package indi.midreamsheep.vegetable.backend.features.report.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.report.domain.ReportDomainService;
import indi.midreamsheep.vegetable.backend.features.report.domain.ReportStatus;
import indi.midreamsheep.vegetable.backend.features.report.domain.ReportTargetType;
import indi.midreamsheep.vegetable.backend.features.report.domain.model.ReportData;
import indi.midreamsheep.vegetable.backend.features.report.domain.query.ReportQuery;
import indi.midreamsheep.vegetable.backend.features.report.presentation.dto.ReportResponse;
import indi.midreamsheep.vegetable.backend.features.report.presentation.dto.ReportUpdateStatusRequest;
import indi.midreamsheep.vegetable.backend.infrastructure.security.AdminAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 举报管理端接口。
 */
@RestController
@RequestMapping("/api/v1/admin/reports")
public class ReportAdminController {

    private final ReportDomainService reportDomainService;
    private final AdminAuthorizationService adminAuthorizationService;

    /**
     * 构造举报管理端控制器。
     *
     * @param reportDomainService 举报领域服务
     * @param adminAuthorizationService 管理员权限服务
     */
    public ReportAdminController(
            ReportDomainService reportDomainService,
            AdminAuthorizationService adminAuthorizationService
    ) {
        this.reportDomainService = reportDomainService;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    /**
     * 举报列表（分页）。
     *
     * @param targetType 目标类型
     * @param status 状态
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @GetMapping
    public ApiResponse<PageResponse<ReportResponse>> list(
            @RequestParam(value = "targetType", required = false) ReportTargetType targetType,
            @RequestParam(value = "status", required = false) ReportStatus status,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        adminAuthorizationService.requireAdmin();
        int finalPage = normalizePage(page);
        int finalPageSize = normalizePageSize(pageSize);
        ReportQuery query = new ReportQuery(targetType, status, finalPage, finalPageSize);
        PageResponse<ReportData> result = reportDomainService.list(query);
        List<ReportResponse> items = result.items().stream()
                .map(ReportAdminController::toResponse)
                .toList();
        return ApiResponse.ok(new PageResponse<>(items, result.page(), result.pageSize(), result.total()));
    }

    /**
     * 更新举报状态（RESOLVED/REJECTED）。
     *
     * @param id 举报ID
     * @param request 更新请求
     * @return 统一响应体
     */
    @PostMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(
            @PathVariable("id") long id,
            @Valid @RequestBody ReportUpdateStatusRequest request
    ) {
        long handlerId = adminAuthorizationService.requireAdminUserId();
        if (request.status() == ReportStatus.OPEN) {
            throw new BizException(ErrorCode.BAD_REQUEST, "status 不合法");
        }
        reportDomainService.updateStatus(id, request.status(), handlerId, request.note());
        return ApiResponse.ok();
    }

    /**
     * 转换为响应 DTO。
     *
     * @param data 领域数据
     * @return 响应 DTO
     */
    private static ReportResponse toResponse(ReportData data) {
        return new ReportResponse(
                data.id(),
                data.reporterId(),
                data.targetType(),
                data.targetId(),
                data.reason(),
                data.status(),
                data.handlerId(),
                data.handledAt(),
                data.handlingNote(),
                data.createdAt()
        );
    }

    /**
     * 规范化页码。
     *
     * @param page 页码
     * @return 页码
     */
    private static int normalizePage(Integer page) {
        if (page == null || page < 1) {
            throw new BizException(ErrorCode.BAD_REQUEST, "page 必须从 1 开始");
        }
        return page;
    }

    /**
     * 规范化页大小。
     *
     * @param pageSize 页大小
     * @return 页大小
     */
    private static int normalizePageSize(Integer pageSize) {
        if (pageSize == null) {
            return 20;
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BizException(ErrorCode.BAD_REQUEST, "pageSize 必须在 1-100 范围内");
        }
        return pageSize;
    }
}

