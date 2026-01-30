package indi.midreamsheep.vegetable.backend.features.problem.domain;

/**
 * 可见性策略。
 */
public enum Visibility {
    /**
     * 公开：可被列表与搜索索引。
     */
    PUBLIC,
    /**
     * 仅链接：不出现在列表/搜索；需凭 shareKey 访问。
     */
    UNLISTED,
    /**
     * 私有：仅作者与管理员可访问。
     */
    PRIVATE
}

