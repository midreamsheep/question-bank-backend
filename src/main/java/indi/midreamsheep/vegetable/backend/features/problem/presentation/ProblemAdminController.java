package indi.midreamsheep.vegetable.backend.features.problem.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemDomainService;
import indi.midreamsheep.vegetable.backend.features.problem.domain.model.ProblemDetailData;
import indi.midreamsheep.vegetable.backend.features.problem.presentation.dto.ProblemStatusResponse;
import indi.midreamsheep.vegetable.backend.infrastructure.security.AdminAuthorizationService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 题目管理端接口。
 */
@RestController
@RequestMapping("/api/v1/admin/problems")
public class ProblemAdminController {

    private final ProblemDomainService problemDomainService;
    private final AdminAuthorizationService adminAuthorizationService;

    /**
     * 构造题目管理端控制器。
     *
     * @param problemDomainService 题目领域服务
     * @param adminAuthorizationService 管理员权限校验
     */
    public ProblemAdminController(
            ProblemDomainService problemDomainService,
            AdminAuthorizationService adminAuthorizationService
    ) {
        this.problemDomainService = problemDomainService;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    /**
     * 下架题目（管理员）。
     *
     * @param id 题目ID
     * @return 统一响应体（更新后的状态）
     */
    @PostMapping("/{id}/disable")
    public ApiResponse<ProblemStatusResponse> disable(@PathVariable("id") long id) {
        adminAuthorizationService.requireAdmin();
        ProblemDetailData updated = problemDomainService.disable(id);
        return ApiResponse.ok(new ProblemStatusResponse(
                updated.id(),
                updated.status(),
                updated.visibility(),
                updated.shareKey()
        ));
    }
}
