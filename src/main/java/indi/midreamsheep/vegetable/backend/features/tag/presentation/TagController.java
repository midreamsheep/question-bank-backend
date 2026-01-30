package indi.midreamsheep.vegetable.backend.features.tag.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.features.tag.domain.TagDomainService;
import indi.midreamsheep.vegetable.backend.features.tag.domain.command.TagCreateCommand;
import indi.midreamsheep.vegetable.backend.features.tag.domain.model.TagData;
import indi.midreamsheep.vegetable.backend.features.tag.presentation.dto.TagCreateRequest;
import indi.midreamsheep.vegetable.backend.features.tag.presentation.dto.TagResponse;
import indi.midreamsheep.vegetable.backend.infrastructure.security.AdminAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 标签接口（列表与管理）。
 */
@RestController
@RequestMapping("/api/v1")
public class TagController {

    private final TagDomainService tagDomainService;
    private final AdminAuthorizationService adminAuthorizationService;

    /**
     * 构造标签控制器。
     *
     * @param tagDomainService 标签领域服务
     */
    public TagController(
            TagDomainService tagDomainService,
            AdminAuthorizationService adminAuthorizationService
    ) {
        this.tagDomainService = tagDomainService;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    /**
     * 标签列表。
     *
     * @param subject 学科
     * @param keyword 关键字
     * @return 标签列表
     */
    @GetMapping("/tags")
    public ApiResponse<List<TagResponse>> list(
            @RequestParam(value = "subject", required = false) String subject,
            @RequestParam(value = "keyword", required = false) String keyword
    ) {
        List<TagResponse> items = tagDomainService.list(subject, keyword).stream()
                .map(TagController::toResponse)
                .toList();
        return ApiResponse.ok(items);
    }

    /**
     * 创建标签（管理端）。
     *
     * @param request 创建请求
     * @return 创建后的标签
     */
    @PostMapping("/admin/tags")
    public ApiResponse<TagResponse> create(@Valid @RequestBody TagCreateRequest request) {
        adminAuthorizationService.requireAdmin();
        TagCreateCommand command = new TagCreateCommand(request.subject().trim(), request.name());
        long id = tagDomainService.create(command);
        TagData data = new TagData(id, command.subject(), command.name());
        return ApiResponse.ok(toResponse(data));
    }

    /**
     * 将领域数据转换为响应 DTO。
     *
     * @param data 标签数据
     * @return 标签响应
     */
    private static TagResponse toResponse(TagData data) {
        return new TagResponse(
                data.id(),
                data.subject(),
                data.name()
        );
    }
}
