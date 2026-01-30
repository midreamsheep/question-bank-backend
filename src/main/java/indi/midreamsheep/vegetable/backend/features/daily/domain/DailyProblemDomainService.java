package indi.midreamsheep.vegetable.backend.features.daily.domain;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.daily.domain.command.DailyProblemPublishCommand;
import indi.midreamsheep.vegetable.backend.features.daily.domain.model.DailyProblemData;
import indi.midreamsheep.vegetable.backend.features.daily.domain.model.DailyProblemProblemSummary;
import indi.midreamsheep.vegetable.backend.features.daily.domain.model.DailyProblemView;
import indi.midreamsheep.vegetable.backend.features.daily.domain.port.DailyProblemRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.problem.domain.ProblemStatus;
import indi.midreamsheep.vegetable.backend.features.problem.domain.Visibility;
import indi.midreamsheep.vegetable.backend.features.problem.domain.port.ProblemRepositoryPort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 每日一题领域服务。
 */
public class DailyProblemDomainService {

    private final DailyProblemRepositoryPort dailyProblemRepositoryPort;
    private final ProblemRepositoryPort problemRepositoryPort;

    /**
     * 构造每日一题领域服务。
     *
     * @param dailyProblemRepositoryPort 每日一题仓储端口
     * @param problemRepositoryPort 题目仓储端口
     */
    public DailyProblemDomainService(
            DailyProblemRepositoryPort dailyProblemRepositoryPort,
            ProblemRepositoryPort problemRepositoryPort
    ) {
        this.dailyProblemRepositoryPort = dailyProblemRepositoryPort;
        this.problemRepositoryPort = problemRepositoryPort;
    }

    /**
     * 获取指定日期的每日一题。
     *
     * @param day 日期
     * @return 每日一题展示数据列表
     */
    public List<DailyProblemView> getByDay(LocalDate day) {
        List<DailyProblemData> items = dailyProblemRepositoryPort.findByDay(day).stream()
                .filter(item -> item.status() == DailyProblemStatus.PUBLISHED)
                .toList();
        if (items.isEmpty()) {
            throw new BizException(ErrorCode.NOT_FOUND, "每日一题不存在");
        }
        return items.stream().map(this::buildView).toList();
    }

    /**
     * 查询每日一题列表。
     *
     * @param from 起始日期
     * @param to 截止日期
     * @param page 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageResponse<DailyProblemView> list(LocalDate from, LocalDate to, int page, int pageSize) {
        PageResponse<DailyProblemData> result = dailyProblemRepositoryPort.list(from, to, page, pageSize);
        return new PageResponse<>(
                result.items().stream().map(this::buildView).toList(),
                result.page(),
                result.pageSize(),
                result.total()
        );
    }

    /**
     * 发布或替换每日一题。
     *
     * @param command 发布命令
     * @return 发布后的每日一题
     */
    public DailyProblemView publish(DailyProblemPublishCommand command) {
        validate(command);
        ensureProblemPublished(command.problemId());
        LocalDateTime now = LocalDateTime.now();
        DailyProblemData data = new DailyProblemData(
                0L,
                command.day(),
                command.problemId(),
                DailyProblemStatus.PUBLISHED,
                command.copywriting(),
                command.operatorId(),
                now,
                null
        );
        DailyProblemData saved = dailyProblemRepositoryPort.publish(data);
        return buildView(saved);
    }

    /**
     * 撤回每日一题。
     *
     * @param day 日期
     * @param operatorId 操作人
     * @return 撤回后的每日一题
     */
    public List<DailyProblemView> revoke(LocalDate day, long operatorId) {
        if (day == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "day 不能为空");
        }
        if (operatorId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "operatorId 不合法");
        }
        List<DailyProblemData> revoked = dailyProblemRepositoryPort.revoke(day, operatorId);
        return revoked.stream().map(this::buildView).toList();
    }

    /**
     * 按 ID 撤回每日一题。
     *
     * @param id ID
     * @param operatorId 操作人
     * @return 撤回后的每日一题
     */
    public DailyProblemView revokeById(long id, long operatorId) {
        if (id <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        if (operatorId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "operatorId 不合法");
        }
        DailyProblemData data = dailyProblemRepositoryPort.revokeById(id, operatorId);
        return buildView(data);
    }

    /**
     * 构建每日一题展示数据。
     *
     * @param data 每日一题数据
     * @return 展示数据
     */
    private DailyProblemView buildView(DailyProblemData data) {
        var detail = problemRepositoryPort.findById(data.problemId())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题目不存在"));
        if (detail.status() != ProblemStatus.PUBLISHED || detail.visibility() != Visibility.PUBLIC) {
            throw new BizException(ErrorCode.NOT_FOUND, "题目不存在");
        }
        DailyProblemProblemSummary summary = new DailyProblemProblemSummary(
                detail.id(),
                detail.title(),
                detail.subject(),
                detail.difficulty()
        );
        return new DailyProblemView(data.id(), data.day(), data.copywriting(), summary);
    }

    /**
     * 校验题目已发布且可公开访问。
     *
     * @param problemId 题目ID
     */
    private void ensureProblemPublished(long problemId) {
        var detail = problemRepositoryPort.findById(problemId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题目不存在"));
        if (detail.status() != ProblemStatus.PUBLISHED || detail.visibility() != Visibility.PUBLIC) {
            throw new BizException(ErrorCode.BAD_REQUEST, "题目未发布或不可见");
        }
    }

    /**
     * 校验发布命令。
     *
     * @param command 发布命令
     */
    private static void validate(DailyProblemPublishCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.day() == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "day 不能为空");
        }
        if (command.problemId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "problemId 不合法");
        }
        if (command.operatorId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "operatorId 不合法");
        }
    }
}
