package indi.midreamsheep.vegetable.backend.features.category.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.features.category.domain.CategoryDomainService;
import indi.midreamsheep.vegetable.backend.features.category.domain.command.CategoryCreateCommand;
import indi.midreamsheep.vegetable.backend.features.category.domain.command.CategoryUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.category.domain.model.CategoryData;
import indi.midreamsheep.vegetable.backend.features.category.presentation.dto.CategoryCreateRequest;
import indi.midreamsheep.vegetable.backend.features.category.presentation.dto.CategoryResponse;
import indi.midreamsheep.vegetable.backend.features.category.presentation.dto.CategoryUpdateRequest;
import indi.midreamsheep.vegetable.backend.infrastructure.security.AdminAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类接口（列表与管理）。
 */
@RestController
@RequestMapping("/api/v1")
public class CategoryController {

    private final CategoryDomainService categoryDomainService;
    private final AdminAuthorizationService adminAuthorizationService;

    /**
     * 构造分类控制器。
     *
     * @param categoryDomainService 分类领域服务
     */
    public CategoryController(
            CategoryDomainService categoryDomainService,
            AdminAuthorizationService adminAuthorizationService
    ) {
        this.categoryDomainService = categoryDomainService;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    /**
     * 分类列表。
     *
     * @param subject 学科
     * @return 分类列表
     */
    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> list(@RequestParam(value = "subject", required = false) String subject) {
        List<CategoryResponse> items = categoryDomainService.list(subject).stream()
                .map(CategoryController::toResponse)
                .toList();
        return ApiResponse.ok(items);
    }

    /**
     * 创建分类（管理端）。
     *
     * @param request 创建请求
     * @return 创建后的分类
     */
    @PostMapping("/admin/categories")
    public ApiResponse<CategoryResponse> create(@Valid @RequestBody CategoryCreateRequest request) {
        adminAuthorizationService.requireAdmin();
        CategoryCreateCommand command = new CategoryCreateCommand(
                request.subject(),
                request.parentId(),
                request.name(),
                request.description(),
                request.sortOrder() == null ? 0 : request.sortOrder(),
                request.enabled() == null || request.enabled()
        );
        long id = categoryDomainService.create(command);
        CategoryData data = new CategoryData(
                id,
                command.subject(),
                command.parentId(),
                command.name(),
                command.description(),
                command.sortOrder(),
                command.enabled()
        );
        return ApiResponse.ok(toResponse(data));
    }

    /**
     * 更新分类（管理端）。
     *
     * @param id 分类ID
     * @param request 更新请求
     * @return 更新后的分类
     */
    @PutMapping("/admin/categories/{id}")
    public ApiResponse<CategoryResponse> update(
            @PathVariable("id") long id,
            @Valid @RequestBody CategoryUpdateRequest request
    ) {
        adminAuthorizationService.requireAdmin();
        CategoryUpdateCommand command = new CategoryUpdateCommand(
                id,
                request.subject(),
                request.parentId(),
                request.name(),
                request.description(),
                request.sortOrder() == null ? 0 : request.sortOrder(),
                request.enabled() == null || request.enabled()
        );
        CategoryData data = categoryDomainService.update(command);
        return ApiResponse.ok(toResponse(data));
    }

    /**
     * 将领域数据转换为响应 DTO。
     *
     * @param data 分类数据
     * @return 分类响应
     */
    private static CategoryResponse toResponse(CategoryData data) {
        return new CategoryResponse(
                data.id(),
                data.subject(),
                data.parentId(),
                data.name(),
                data.description(),
                data.sortOrder(),
                data.enabled()
        );
    }
}
