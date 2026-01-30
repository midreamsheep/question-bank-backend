package indi.midreamsheep.vegetable.backend.features.problem.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 题目接口测试：验证 Markdown/LaTeX 提交格式。
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProblemControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 提交 Markdown 题目成功。
     *
     * @throws Exception 测试异常
     */
    @Test
    void create_markdown_problem_success() throws Exception {
        mockMvc.perform(post("/api/v1/problems")
                        .with(user("1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "一道不等式题",
                                  "subject": "MATH",
                                  "difficulty": 3,
                                  "statementFormat": "MARKDOWN",
                                  "statement": "题干 **Markdown**",
                                  "solutionFormat": "MARKDOWN",
                                  "solution": "解答 *Markdown*",
                                  "visibility": "PUBLIC"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.visibility").value("PUBLIC"));
    }

    /**
     * 提交 LaTeX 题目成功（仅链接）。
     *
     * @throws Exception 测试异常
     */
    @Test
    void create_latex_problem_success() throws Exception {
        mockMvc.perform(post("/api/v1/problems")
                        .with(user("1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "一道物理题",
                                  "subject": "PHYSICS",
                                  "difficulty": 4,
                                  "statementFormat": "LATEX",
                                  "statement": "\\\\text{题干}",
                                  "solutionFormat": "LATEX",
                                  "solution": "\\\\text{解答}",
                                  "visibility": "UNLISTED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.visibility").value("UNLISTED"))
                .andExpect(jsonPath("$.data.shareKey").isString());
    }
}
