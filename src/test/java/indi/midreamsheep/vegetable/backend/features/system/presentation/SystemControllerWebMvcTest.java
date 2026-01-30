package indi.midreamsheep.vegetable.backend.features.system.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 系统接口测试：验证健康检查响应体格式。
 */
@SpringBootTest
@AutoConfigureMockMvc
class SystemControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 健康检查接口返回统一成功响应体。
     *
     * @throws Exception 测试异常
     */
    @Test
    void health_returnsOkEnvelope() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("OK"));
    }
}
