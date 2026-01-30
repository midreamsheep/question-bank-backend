package indi.midreamsheep.vegetable.backend.features.problem.domain.command;

import indi.midreamsheep.vegetable.backend.features.problem.domain.ContentFormat;
import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目更新命令。
 *
 * @param id 题目ID
 * @param authorId 作者ID
 * @param title 标题
 * @param subject 学科
 * @param difficulty 难度
 * @param statementFormat 题干格式
 * @param statementContent 题干内容
 * @param solutionFormat 解答格式
 * @param solutionContent 解答内容
 * @param visibility 可见性
 * @param shareKey 分享 key
 * @param status 状态
 * @param publishedAt 发布时间
 * @param tagIds 标签ID列表
 */
public record ProblemUpdateCommand(
        long id,
        long authorId,
        String title,
        String subject,
        int difficulty,
        ContentFormat statementFormat,
        String statementContent,
        ContentFormat solutionFormat,
        String solutionContent,
        Visibility visibility,
        String shareKey,
        ProblemStatus status,
        LocalDateTime publishedAt,
        List<Long> tagIds
) {
}
