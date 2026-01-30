package indi.midreamsheep.vegetable.backend.features.report.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.report.domain.ReportDomainService;
import indi.midreamsheep.vegetable.backend.features.report.domain.command.ReportCreateCommand;
import indi.midreamsheep.vegetable.backend.features.report.presentation.dto.ReportCreateRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 举报接口（用户端）。
 */
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportDomainService reportDomainService;

    /**
     * 构造举报控制器。
     *
     * @param reportDomainService 举报领域服务
     */
    public ReportController(ReportDomainService reportDomainService) {
        this.reportDomainService = reportDomainService;
    }

    /**
     * 创建举报。
     *
     * @param request 创建请求
     * @return 统一响应体（举报ID）
     */
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody ReportCreateRequest request) {
        long reporterId = requireCurrentUserId();
        if (request.targetId() == null || request.targetId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "targetId 不合法");
        }
        long id = reportDomainService.create(new ReportCreateCommand(
                reporterId,
                request.targetType(),
                request.targetId(),
                request.reason()
        ));
        return ApiResponse.ok(id);
    }

    /**
     * 获取当前用户 ID（必须已登录）。
     *
     * @return 用户ID
     */
    private static long requireCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未登录");
        }
        Object principalObj = authentication.getPrincipal();
        String principal = (principalObj instanceof UserDetails userDetails)
                ? userDetails.getUsername()
                : String.valueOf(principalObj);
        try {
            long userId = Long.parseLong(principal);
            if (userId <= 0) {
                throw new NumberFormatException("id <= 0");
            }
            return userId;
        } catch (NumberFormatException ex) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未登录");
        }
    }
}

