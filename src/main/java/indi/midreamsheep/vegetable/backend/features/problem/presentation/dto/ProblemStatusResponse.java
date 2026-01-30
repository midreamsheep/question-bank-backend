package indi.midreamsheep.vegetable.backend.features.problem.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;

/**
 * 题目状态响应 DTO。
 *
 * @param id 题目ID
 * @param status 状态
 * @param visibility 可见性
 * @param shareKey 分享 key
 */
public record ProblemStatusResponse(
        long id,
        ProblemStatus status,
        Visibility visibility,
        String shareKey
) {
}
