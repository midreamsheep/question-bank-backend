package indi.midreamsheep.vegetable.backend.features.file.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 文件接口测试：在 MinIO 未启用时返回明确错误提示。
 */
@SpringBootTest
@AutoConfigureMockMvc
class FileControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * MinIO 未启用时，上传文件返回 503。
     *
     * @throws Exception 测试异常
     */
    @Test
    void upload_returns503_whenMinioDisabled() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "a.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "hello".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/files")
                        .file(file)
                        .with(user("demo")))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value(50300))
                .andExpect(jsonPath("$.message").value("MinIO 未启用，请配置 minio.enabled=true"));
    }

    /**
     * MinIO 未启用时，获取预签名 URL 返回 503。
     *
     * @throws Exception 测试异常
     */
    @Test
    void presignedGetUrl_returns503_whenMinioDisabled() throws Exception {
        mockMvc.perform(get("/api/v1/files/presigned-get-url")
                        .param("objectKey", "user/demo/20260101/xxx-a.txt")
                        .with(user("demo")))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value(50300))
                .andExpect(jsonPath("$.message").value("MinIO 未启用，请配置 minio.enabled=true"));
    }
}

