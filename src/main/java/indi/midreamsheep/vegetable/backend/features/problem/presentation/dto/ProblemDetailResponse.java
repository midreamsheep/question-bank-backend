package indi.midreamsheep.vegetable.backend.features.problem.presentation.dto;

import indi.midreamsheep.vegetable.backend.features.problem.domain.ContentFormat;
import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;

import java.util.List;

/**
 * 题目详情响应 DTO。
 *
 * @param id 题目ID
 * @param title 标题
 * @param subject 学科
 * @param difficulty 难度
 * @param statementFormat 题干格式
 * @param statement 题干内容
 * @param solutionFormat 解答格式
 * @param solution 解答内容
 * @param visibility 可见性
 * @param shareKey 分享 key
 * @param status 状态
 * @param author 作者信息
 * @param tagIds 标签ID列表
 * @param tags 标签对象列表（便于前端直接展示）
 */
public record ProblemDetailResponse(
        long id,
        String title,
        String subject,
        int difficulty,
        ContentFormat statementFormat,
        String statement,
        ContentFormat solutionFormat,
        String solution,
        Visibility visibility,
        String shareKey,
        ProblemStatus status,
        ProblemAuthorResponse author,
        List<Long> tagIds,
        List<ProblemTagResponse> tags
) {
}
