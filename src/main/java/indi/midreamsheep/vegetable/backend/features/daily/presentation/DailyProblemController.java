package indi.midreamsheep.vegetable.backend.features.daily.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.daily.domain.DailyProblemDomainService;
import indi.midreamsheep.vegetable.backend.features.daily.domain.command.DailyProblemPublishCommand;
import indi.midreamsheep.vegetable.backend.features.daily.domain.model.DailyProblemProblemSummary;
import indi.midreamsheep.vegetable.backend.features.daily.domain.model.DailyProblemView;
import indi.midreamsheep.vegetable.backend.features.daily.presentation.dto.DailyProblemProblemResponse;
import indi.midreamsheep.vegetable.backend.features.daily.presentation.dto.DailyProblemPublishRequest;
import indi.midreamsheep.vegetable.backend.features.daily.presentation.dto.DailyProblemResponse;
import indi.midreamsheep.vegetable.backend.infrastructure.security.AdminAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 每日一题接口。
 */
@RestController
@RequestMapping("/api/v1")
public class DailyProblemController {

    private final DailyProblemDomainService dailyProblemDomainService;
    private final AdminAuthorizationService adminAuthorizationService;

    /**
     * 构造每日一题控制器。
     *
     * @param dailyProblemDomainService 每日一题领域服务
     */
    public DailyProblemController(
            DailyProblemDomainService dailyProblemDomainService,
            AdminAuthorizationService adminAuthorizationService
    ) {
        this.dailyProblemDomainService = dailyProblemDomainService;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    /**
     * 今日每日一题。
     *
     * @return 统一响应体
     */
    @GetMapping("/daily-problem/today")
    public ApiResponse<List<DailyProblemResponse>> today() {
        List<DailyProblemView> views = dailyProblemDomainService.getByDay(LocalDate.now());
        return ApiResponse.ok(views.stream().map(DailyProblemController::toResponse).toList());
    }

    /**
     * 指定日期每日一题。
     *
     * @param day 日期
     * @return 统一响应体
     */
    @GetMapping("/daily-problem")
    public ApiResponse<List<DailyProblemResponse>> byDay(@RequestParam("day") LocalDate day) {
        List<DailyProblemView> views = dailyProblemDomainService.getByDay(day);
        return ApiResponse.ok(views.stream().map(DailyProblemController::toResponse).toList());
    }

    /**
     * 每日一题历史列表。
     *
     * @param from 起始日期
     * @param to 截止日期
     * @param page 页码
     * @param pageSize 每页大小
     * @return 统一响应体（分页）
     */
    @GetMapping("/daily-problems")
    public ApiResponse<PageResponse<DailyProblemResponse>> list(
            @RequestParam(value = "from", required = false) LocalDate from,
            @RequestParam(value = "to", required = false) LocalDate to,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        int finalPage = normalizePage(page);
        int finalPageSize = normalizePageSize(pageSize);
        validateDateRange(from, to);
        PageResponse<DailyProblemView> result = dailyProblemDomainService.list(from, to, finalPage, finalPageSize);
        PageResponse<DailyProblemResponse> mapped = new PageResponse<>(
                result.items().stream().map(DailyProblemController::toResponse).toList(),
                result.page(),
                result.pageSize(),
                result.total()
        );
        return ApiResponse.ok(mapped);
    }

    /**
     * 发布或替换每日一题（登录用户可用）。
     *
     * @param request 发布请求
     * @return 统一响应体
     */
    @PostMapping("/admin/daily-problems")
    public ApiResponse<DailyProblemResponse> publish(@Valid @RequestBody DailyProblemPublishRequest request) {
        long operatorId = requireCurrentUserId();
        DailyProblemPublishCommand command = new DailyProblemPublishCommand(
                request.day(),
                request.problemId(),
                request.copywriting(),
                operatorId
        );
        DailyProblemView view = dailyProblemDomainService.publish(command);
        return ApiResponse.ok(toResponse(view));
    }

    /**
     * 撤回每日一题（管理端）。
     *
     * @param day 日期
     * @return 统一响应体
     */
    @PostMapping("/admin/daily-problems/{day}/revoke")
    public ApiResponse<List<DailyProblemResponse>> revoke(@PathVariable("day") LocalDate day) {
        long operatorId = adminAuthorizationService.requireAdminUserId();
        List<DailyProblemView> views = dailyProblemDomainService.revoke(day, operatorId);
        return ApiResponse.ok(views.stream().map(DailyProblemController::toResponse).toList());
    }

    /**
     * 撤回每日一题（管理端，按 ID 撤回单条）。
     *
     * @param id ID
     * @return 统一响应体
     */
    @PostMapping("/admin/daily-problem-items/{id}/revoke")
    public ApiResponse<DailyProblemResponse> revokeById(@PathVariable("id") long id) {
        long operatorId = adminAuthorizationService.requireAdminUserId();
        DailyProblemView view = dailyProblemDomainService.revokeById(id, operatorId);
        return ApiResponse.ok(toResponse(view));
    }

    /**
     * 将领域数据转换为响应 DTO。
     *
     * @param view 展示数据
     * @return 响应 DTO
     */
    private static DailyProblemResponse toResponse(DailyProblemView view) {
        DailyProblemProblemSummary summary = view.problem();
        DailyProblemProblemResponse problem = new DailyProblemProblemResponse(
                summary.id(),
                summary.title(),
                summary.subject(),
                summary.difficulty()
        );
        return new DailyProblemResponse(view.id(), view.day(), view.copywriting(), problem);
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

    /**
     * 校验日期范围。
     *
     * @param from 起始日期
     * @param to 截止日期
     */
    private static void validateDateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "from 不能晚于 to");
        }
    }

    /**
     * 获取当前用户 ID（从认证上下文获取）。
     *
     * @return 用户ID
     */
    private static long requireCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "未认证用户");
        }
        Object principalObj = authentication.getPrincipal();
        String principal = (principalObj instanceof UserDetails userDetails)
                ? userDetails.getUsername()
                : String.valueOf(principalObj);
        try {
            return Long.parseLong(principal);
        } catch (NumberFormatException ex) {
            throw new BizException(ErrorCode.BAD_REQUEST, "用户ID不合法");
        }
    }
}
