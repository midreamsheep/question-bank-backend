package indi.midreamsheep.vegetable.backend.features.collection.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.collection.domain.CollectionDomainService;
import indi.midreamsheep.vegetable.backend.features.collection.domain.CollectionStatus;
import indi.midreamsheep.vegetable.backend.features.collection.domain.command.CollectionAddItemCommand;
import indi.midreamsheep.vegetable.backend.features.collection.domain.command.CollectionCreateCommand;
import indi.midreamsheep.vegetable.backend.features.collection.domain.command.CollectionRemoveItemCommand;
import indi.midreamsheep.vegetable.backend.features.collection.domain.command.CollectionReorderCommand;
import indi.midreamsheep.vegetable.backend.features.collection.domain.command.CollectionUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionDetailData;
import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionItemData;
import indi.midreamsheep.vegetable.backend.features.collection.domain.model.CollectionSummaryData;
import indi.midreamsheep.vegetable.backend.features.collection.presentation.dto.CollectionCreateRequest;
import indi.midreamsheep.vegetable.backend.features.collection.presentation.dto.CollectionCreateResponse;
import indi.midreamsheep.vegetable.backend.features.collection.presentation.dto.CollectionDetailResponse;
import indi.midreamsheep.vegetable.backend.features.collection.presentation.dto.CollectionItemRequest;
import indi.midreamsheep.vegetable.backend.features.collection.presentation.dto.CollectionItemResponse;
import indi.midreamsheep.vegetable.backend.features.collection.presentation.dto.CollectionReorderRequest;
import indi.midreamsheep.vegetable.backend.features.collection.presentation.dto.CollectionSummaryResponse;
import indi.midreamsheep.vegetable.backend.features.collection.presentation.dto.CollectionUpdateRequest;
import indi.midreamsheep.vegetable.backend.features.favorite.domain.FavoriteDomainService;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 题单接口。
 */
@RestController
@RequestMapping("/api/v1/collections")
public class CollectionController {

    private final CollectionDomainService collectionDomainService;
    private final FavoriteDomainService favoriteDomainService;

    /**
     * 构造题单控制器。
     *
     * @param collectionDomainService 题单领域服务
     */
    public CollectionController(
            CollectionDomainService collectionDomainService,
            FavoriteDomainService favoriteDomainService
    ) {
        this.collectionDomainService = collectionDomainService;
        this.favoriteDomainService = favoriteDomainService;
    }

    /**
     * 创建题单。
     *
     * @param request 创建请求
     * @return 统一响应体（题单ID与状态）
     */
    @PostMapping
    public ApiResponse<CollectionCreateResponse> create(@Valid @RequestBody CollectionCreateRequest request) {
        long authorId = requireCurrentUserId();
        CollectionCreateCommand command = new CollectionCreateCommand(
                authorId,
                request.name(),
                request.description(),
                request.visibility(),
                request.visibility() == Visibility.UNLISTED ? CollectionDomainService.generateShareKey() : null
        );
        long id = collectionDomainService.create(command);
        return ApiResponse.ok(new CollectionCreateResponse(
                id,
                CollectionStatus.ACTIVE,
                request.visibility(),
                command.shareKey()
        ));
    }

    /**
     * 获取公开题单列表（分页）。
     *
     * @param page 页码
     * @param pageSize 每页大小
     * @return 统一响应体（分页）
     */
    @GetMapping
    public ApiResponse<PageResponse<CollectionSummaryResponse>> list(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
    ) {
        int finalPage = normalizePage(page);
        int finalPageSize = normalizePageSize(pageSize);
        PageResponse<CollectionSummaryData> result = collectionDomainService.listPublic(finalPage, finalPageSize);
        List<CollectionSummaryResponse> items = result.items().stream()
                .map(CollectionController::toSummaryResponse)
                .toList();
        return ApiResponse.ok(new PageResponse<>(items, result.page(), result.pageSize(), result.total()));
    }

    /**
     * 获取题单详情。
     *
     * @param id 题单ID
     * @return 统一响应体（题单详情）
     */
    @GetMapping("/{id}")
    public ApiResponse<CollectionDetailResponse> detail(@PathVariable("id") long id) {
        Long requesterId = currentUserIdOrNull();
        CollectionDetailData detail = collectionDomainService.getDetail(id, requesterId);
        return ApiResponse.ok(toDetailResponse(detail));
    }

    /**
     * 通过分享 key 获取题单详情。
     *
     * @param shareKey 分享 key
     * @return 统一响应体（题单详情）
     */
    @GetMapping("/share/{shareKey}")
    public ApiResponse<CollectionDetailResponse> detailByShareKey(@PathVariable("shareKey") String shareKey) {
        if (!StringUtils.hasText(shareKey)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "shareKey 不能为空");
        }
        CollectionDetailData detail = collectionDomainService.getDetailByShareKey(shareKey);
        return ApiResponse.ok(toDetailResponse(detail));
    }

    /**
     * 更新题单（仅作者）。
     *
     * @param id 题单ID
     * @param request 更新请求
     * @return 统一响应体（题单详情）
     */
    @PutMapping("/{id}")
    public ApiResponse<CollectionDetailResponse> update(
            @PathVariable("id") long id,
            @Valid @RequestBody CollectionUpdateRequest request
    ) {
        long authorId = requireCurrentUserId();
        CollectionUpdateCommand command = new CollectionUpdateCommand(
                id,
                authorId,
                request.name(),
                request.description(),
                request.visibility(),
                null
        );
        CollectionDetailData updated = collectionDomainService.update(command);
        return ApiResponse.ok(toDetailResponse(updated));
    }

    /**
     * 删除题单（仅作者，软删除）。
     *
     * @param id 题单ID
     * @return 统一响应体
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") long id) {
        long authorId = requireCurrentUserId();
        collectionDomainService.delete(id, authorId);
        return ApiResponse.ok();
    }

    /**
     * 添加题目到题单。
     *
     * @param id 题单ID
     * @param request 题单条目
     * @return 统一响应体
     */
    @PostMapping("/{id}/items")
    public ApiResponse<Void> addItem(
            @PathVariable("id") long id,
            @Valid @RequestBody CollectionItemRequest request
    ) {
        long authorId = requireCurrentUserId();
        CollectionAddItemCommand command = new CollectionAddItemCommand(
                id,
                authorId,
                request.problemId(),
                request.sortOrder()
        );
        collectionDomainService.addItem(command);
        return ApiResponse.ok();
    }

    /**
     * 调整题单条目顺序。
     *
     * @param id 题单ID
     * @param request 调整请求
     * @return 统一响应体
     */
    @PutMapping("/{id}/items/reorder")
    public ApiResponse<Void> reorder(
            @PathVariable("id") long id,
            @Valid @RequestBody CollectionReorderRequest request
    ) {
        long authorId = requireCurrentUserId();
        List<CollectionItemData> items = request.items().stream()
                .map(item -> new CollectionItemData(item.problemId(), item.sortOrder()))
                .toList();
        CollectionReorderCommand command = new CollectionReorderCommand(id, authorId, items);
        collectionDomainService.reorder(command);
        return ApiResponse.ok();
    }

    /**
     * 从题单移除题目（仅作者）。
     *
     * @param id 题单ID
     * @param problemId 题目ID
     * @return 统一响应体
     */
    @DeleteMapping("/{id}/items/{problemId}")
    public ApiResponse<Void> removeItem(
            @PathVariable("id") long id,
            @PathVariable("problemId") long problemId
    ) {
        long authorId = requireCurrentUserId();
        collectionDomainService.removeItem(new CollectionRemoveItemCommand(id, authorId, problemId));
        return ApiResponse.ok();
    }

    /**
     * 收藏题单。
     *
     * @param id 题单ID
     * @return 统一响应体
     */
    @PostMapping("/{id}/favorite")
    public ApiResponse<Void> favorite(@PathVariable("id") long id) {
        long userId = requireCurrentUserId();
        favoriteDomainService.favoriteCollection(userId, id);
        return ApiResponse.ok();
    }

    /**
     * 取消收藏题单。
     *
     * @param id 题单ID
     * @return 统一响应体
     */
    @DeleteMapping("/{id}/favorite")
    public ApiResponse<Void> unfavorite(@PathVariable("id") long id) {
        long userId = requireCurrentUserId();
        favoriteDomainService.unfavoriteCollection(userId, id);
        return ApiResponse.ok();
    }

    /**
     * 将领域数据转换为响应 DTO。
     *
     * @param data 题单详情
     * @return 响应 DTO
     */
    private static CollectionDetailResponse toDetailResponse(CollectionDetailData data) {
        List<CollectionItemResponse> items = data.items().stream()
                .map(item -> new CollectionItemResponse(item.problemId(), item.sortOrder()))
                .toList();
        return new CollectionDetailResponse(
                data.id(),
                data.name(),
                data.description(),
                data.visibility(),
                data.shareKey(),
                data.status(),
                items
        );
    }

    /**
     * 将领域数据转换为摘要响应 DTO。
     *
     * @param data 题单摘要
     * @return 摘要响应
     */
    private static CollectionSummaryResponse toSummaryResponse(CollectionSummaryData data) {
        return new CollectionSummaryResponse(
                data.id(),
                data.name(),
                data.description(),
                data.visibility(),
                data.status(),
                data.itemCount(),
                data.authorId()
        );
    }

    /**
     * 获取当前用户 ID（必须已登录）。
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
