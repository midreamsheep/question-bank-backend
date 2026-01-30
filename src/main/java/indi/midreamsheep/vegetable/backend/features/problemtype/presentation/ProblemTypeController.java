package indi.midreamsheep.vegetable.backend.features.problemtype.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.ProblemTypeDomainService;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.command.ProblemTypeCreateCommand;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.command.ProblemTypeUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.model.ProblemTypeData;
import indi.midreamsheep.vegetable.backend.features.problemtype.presentation.dto.ProblemTypeCreateRequest;
import indi.midreamsheep.vegetable.backend.features.problemtype.presentation.dto.ProblemTypeResponse;
import indi.midreamsheep.vegetable.backend.features.problemtype.presentation.dto.ProblemTypeUpdateRequest;
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
 * 题型接口（列表与管理）。
 */
@RestController
@RequestMapping("/api/v1")
public class ProblemTypeController {

    private final ProblemTypeDomainService problemTypeDomainService;
    private final AdminAuthorizationService adminAuthorizationService;

    /**
     * 构造题型控制器。
     *
     * @param problemTypeDomainService 题型领域服务
     */
    public ProblemTypeController(
            ProblemTypeDomainService problemTypeDomainService,
            AdminAuthorizationService adminAuthorizationService
    ) {
        this.problemTypeDomainService = problemTypeDomainService;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    /**
     * 题型列表。
     *
     * @param subject 学科
     * @return 题型列表
     */
    @GetMapping("/problem-types")
    public ApiResponse<List<ProblemTypeResponse>> list(
            @RequestParam(value = "subject", required = false) String subject
    ) {
        List<ProblemTypeResponse> items = problemTypeDomainService.list(subject).stream()
                .map(ProblemTypeController::toResponse)
                .toList();
        return ApiResponse.ok(items);
    }

    /**
     * 创建题型（管理端）。
     *
     * @param request 创建请求
     * @return 创建后的题型
     */
    @PostMapping("/admin/problem-types")
    public ApiResponse<ProblemTypeResponse> create(@Valid @RequestBody ProblemTypeCreateRequest request) {
        adminAuthorizationService.requireAdmin();
        ProblemTypeCreateCommand command = new ProblemTypeCreateCommand(
                request.subject(),
                request.name(),
                request.description(),
                request.sortOrder() == null ? 0 : request.sortOrder(),
                request.enabled() == null || request.enabled()
        );
        long id = problemTypeDomainService.create(command);
        ProblemTypeData data = new ProblemTypeData(
                id,
                command.subject(),
                command.name(),
                command.description(),
                command.sortOrder(),
                command.enabled()
        );
        return ApiResponse.ok(toResponse(data));
    }

    /**
     * 更新题型（管理端）。
     *
     * @param id 题型ID
     * @param request 更新请求
     * @return 更新后的题型
     */
    @PutMapping("/admin/problem-types/{id}")
    public ApiResponse<ProblemTypeResponse> update(
            @PathVariable("id") long id,
            @Valid @RequestBody ProblemTypeUpdateRequest request
    ) {
        adminAuthorizationService.requireAdmin();
        ProblemTypeUpdateCommand command = new ProblemTypeUpdateCommand(
                id,
                request.subject(),
                request.name(),
                request.description(),
                request.sortOrder() == null ? 0 : request.sortOrder(),
                request.enabled() == null || request.enabled()
        );
        ProblemTypeData data = problemTypeDomainService.update(command);
        return ApiResponse.ok(toResponse(data));
    }

    /**
     * 将领域数据转换为响应 DTO。
     *
     * @param data 题型数据
     * @return 题型响应
     */
    private static ProblemTypeResponse toResponse(ProblemTypeData data) {
        return new ProblemTypeResponse(
                data.id(),
                data.subject(),
                data.name(),
                data.description(),
                data.sortOrder(),
                data.enabled()
        );
    }
}
