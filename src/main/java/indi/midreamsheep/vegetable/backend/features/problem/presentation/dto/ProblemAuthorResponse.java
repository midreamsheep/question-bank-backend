package indi.midreamsheep.vegetable.backend.features.problem.presentation.dto;

/**
 * 作者信息简要 DTO。
 *
 * @param id 作者ID
 * @param nickname 作者昵称
 * @param displayName 可展示名称（服务端回退后的稳定字段）
 */
public record ProblemAuthorResponse(
        long id,
        String nickname,
        String displayName
) {
}
