package indi.midreamsheep.vegetable.backend.features.comment.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.comment.domain.ProblemCommentDomainService;
import indi.midreamsheep.vegetable.backend.features.comment.domain.model.ProblemCommentData;
import indi.midreamsheep.vegetable.backend.features.comment.presentation.dto.ProblemCommentCreateRequest;
import indi.midreamsheep.vegetable.backend.features.comment.presentation.dto.ProblemCommentResponse;
import indi.midreamsheep.vegetable.backend.infrastructure.security.AdminAuthorizationService;
import jakarta.validation.Valid;
import indi.midreamsheep.vegetable.backend.features.like.domain.LikeCommentDomainService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 题目评论接口。
 */
@RestController
@RequestMapping("/api/v1/problems")
public class ProblemCommentController {

    private final ProblemCommentDomainService problemCommentDomainService;
    private final AdminAuthorizationService adminAuthorizationService;
    private final LikeCommentDomainService likeCommentDomainService;

    /**
     * 构造题目评论控制器。
     *
     * @param problemCommentDomainService 评论领域服务
     * @param adminAuthorizationService 管理员权限服务
     * @param likeCommentDomainService 评论点赞领域服务
     */
    public ProblemCommentController(
            ProblemCommentDomainService problemCommentDomainService,
            AdminAuthorizationService adminAuthorizationService,
            LikeCommentDomainService likeCommentDomainService
    ) {
        this.problemCommentDomainService = problemCommentDomainService;
        this.adminAuthorizationService = adminAuthorizationService;
        this.likeCommentDomainService = likeCommentDomainService;
    }

    /**
     * 评论列表（分页）。
     *
     * @param problemId 题目ID
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @GetMapping("/{problemId}/comments")
    public ApiResponse<PageResponse<ProblemCommentResponse>> list(
            @PathVariable("problemId") long problemId,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        Long requesterId = currentUserIdOrNull();
        PageResponse<ProblemCommentData> result = problemCommentDomainService.list(
                problemId,
                requesterId,
                parentId,
                normalizePage(page),
                normalizePageSize(pageSize)
        );
        List<ProblemCommentResponse> items = result.items().stream()
                .map(ProblemCommentController::toResponse)
                .toList();
        return ApiResponse.ok(new PageResponse<>(items, result.page(), result.pageSize(), result.total()));
    }

    /**
     * 创建评论。
     *
     * @param problemId 题目ID
     * @param request 创建请求
     * @return 统一响应体（评论ID）
     */
    @PostMapping("/{problemId}/comments")
    public ApiResponse<Long> create(
            @PathVariable("problemId") long problemId,
            @Valid @RequestBody ProblemCommentCreateRequest request
    ) {
        long userId = requireCurrentUserId();
        long id = problemCommentDomainService.create(
                problemId,
                userId,
                request.parentId(),
                request.replyToCommentId(),
                request.content()
        );
        return ApiResponse.ok(id);
    }

    /**
     * 删除评论（作者或管理员）。
     *
     * @param problemId 题目ID
     * @param commentId 评论ID
     * @return 统一响应体
     */
    @DeleteMapping("/{problemId}/comments/{commentId}")
    public ApiResponse<Void> delete(
            @PathVariable("problemId") long problemId,
            @PathVariable("commentId") long commentId
    ) {
        long userId = requireCurrentUserId();
        ProblemCommentData comment = problemCommentDomainService.getIncludingDeleted(commentId);
        if (comment.problemId() != problemId) {
            throw new BizException(ErrorCode.NOT_FOUND, "评论不存在");
        }
        if (comment.userId() != userId) {
            adminAuthorizationService.requireAdmin();
        }
        if (!comment.deleted()) {
            problemCommentDomainService.delete(commentId);
        }
        return ApiResponse.ok();
    }

    /**
     * 点赞评论。
     *
     * @param problemId 题目ID
     * @param commentId 评论ID
     * @return 统一响应体
     */
    @PostMapping("/{problemId}/comments/{commentId}/like")
    public ApiResponse<Void> like(
            @PathVariable("problemId") long problemId,
            @PathVariable("commentId") long commentId
    ) {
        long userId = requireCurrentUserId();
        likeCommentDomainService.like(problemId, commentId, userId);
        return ApiResponse.ok();
    }

    /**
     * 取消点赞评论。
     *
     * @param problemId 题目ID
     * @param commentId 评论ID
     * @return 统一响应体
     */
    @DeleteMapping("/{problemId}/comments/{commentId}/like")
    public ApiResponse<Void> unlike(
            @PathVariable("problemId") long problemId,
            @PathVariable("commentId") long commentId
    ) {
        long userId = requireCurrentUserId();
        likeCommentDomainService.unlike(problemId, commentId, userId);
        return ApiResponse.ok();
    }

    /**
     * 转换为响应 DTO。
     *
     * @param data 领域数据
     * @return 响应 DTO
     */
    private static ProblemCommentResponse toResponse(ProblemCommentData data) {
        return new ProblemCommentResponse(
                data.id(),
                data.problemId(),
                data.userId(),
                data.parentId(),
                data.replyToCommentId(),
                data.content(),
                data.likeCount(),
                data.deleted(),
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

    /**
     * 获取当前用户 ID（未登录返回 null）。
     *
     * @return 用户ID 或 null
     */
    private static Long currentUserIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        Object principalObj = authentication.getPrincipal();
        String principal = (principalObj instanceof UserDetails userDetails)
                ? userDetails.getUsername()
                : String.valueOf(principalObj);
        try {
            long userId = Long.parseLong(principal);
            return userId > 0 ? userId : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
