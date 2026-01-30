package indi.midreamsheep.vegetable.backend.features.problem.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;

/**
 * 题目创建响应 DTO。
 *
 * @param id 题目ID
 * @param status 题目状态
 * @param visibility 可见性
 * @param shareKey 仅链接访问凭证（UNLISTED 时返回）
 */
public record ProblemCreateResponse(
        long id,
        ProblemStatus status,
        Visibility visibility,
        String shareKey
) {
}

