package indi.midreamsheep.vegetable.backend.features.auth.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 认证接口测试：验证登录接口返回统一响应体与 token。
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 登录成功时返回包含 token 的响应体。
     *
     * @throws Exception 测试异常
     */
    @Test
    void login_returnsJwtEnvelope() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"demo","password":"demo"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.token").isString());
    }
}
