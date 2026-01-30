package indi.midreamsheep.vegetable.backend.features.system.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统基础接口（健康检查等）。
 */
@RestController
@RequestMapping("/api/v1")
public class SystemController {

    /**
     * 健康检查接口。
     *
     * @return 统一响应体
     */
    @GetMapping("/health")
    public ApiResponse<Void> health() {
        return ApiResponse.ok();
    }
}
