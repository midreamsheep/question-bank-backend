package indi.midreamsheep.vegetable.backend.features.problem.domain.command;

import indi.midreamsheep.vegetable.backend.features.problem.domain.ContentFormat;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;

import java.util.List;

/**
 * 创建题目的命令对象（领域输入）。
 *
 * @param authorId 作者用户ID
 * @param title 标题
 * @param subject 学科
 * @param difficulty 难度（1-5）
 * @param statementFormat 题干格式
 * @param statementContent 题干内容
 * @param solutionFormat 解答格式
 * @param solutionContent 解答内容
 * @param visibility 可见性
 * @param shareKey 仅链接访问凭证（UNLISTED 时由服务端生成）
 * @param tagIds 标签ID列表
 */
public record ProblemCreateCommand(
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
        List<Long> tagIds
) {
}
