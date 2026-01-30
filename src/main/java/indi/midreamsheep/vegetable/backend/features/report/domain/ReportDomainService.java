package indi.midreamsheep.vegetable.backend.features.report.domain;

import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.report.domain.command.ReportCreateCommand;
import indi.midreamsheep.vegetable.backend.features.report.domain.model.ReportData;
import indi.midreamsheep.vegetable.backend.features.report.domain.port.ReportRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.report.domain.query.ReportQuery;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 举报领域服务。
 */
public class ReportDomainService {

    private final ReportRepositoryPort reportRepositoryPort;

    /**
     * 构造举报领域服务。
     *
     * @param reportRepositoryPort 举报仓储端口
     */
    public ReportDomainService(ReportRepositoryPort reportRepositoryPort) {
        this.reportRepositoryPort = reportRepositoryPort;
    }

    /**
     * 创建举报。
     *
     * @param command 创建命令
     * @return 举报ID
     */
    public long create(ReportCreateCommand command) {
        validate(command);
        String finalReason = command.reason().trim();
        ReportCreateCommand finalCommand = new ReportCreateCommand(
                command.reporterId(),
                command.targetType(),
                command.targetId(),
                finalReason
        );
        return reportRepositoryPort.create(finalCommand);
    }

    /**
     * 举报列表（分页，管理端）。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    public PageResponse<ReportData> list(ReportQuery query) {
        if (query == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (query.page() < 1) {
            throw new BizException(ErrorCode.BAD_REQUEST, "page 必须从 1 开始");
        }
        if (query.pageSize() < 1 || query.pageSize() > 100) {
            throw new BizException(ErrorCode.BAD_REQUEST, "pageSize 必须在 1-100 范围内");
        }
        return reportRepositoryPort.list(query);
    }

    /**
     * 更新举报状态（管理端）。
     *
     * @param id 举报ID
     * @param status 新状态
     * @param handlerId 处理人ID
     * @param note 处理备注
     */
    public void updateStatus(long id, ReportStatus status, long handlerId, String note) {
        if (id <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        if (handlerId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "handlerId 不合法");
        }
        if (status == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "status 不能为空");
        }
        if (status == ReportStatus.OPEN) {
            throw new BizException(ErrorCode.BAD_REQUEST, "不可将状态更新为 OPEN");
        }
        String finalNote = null;
        if (StringUtils.hasText(note)) {
            finalNote = note.trim();
            if (finalNote.length() > 1024) {
                throw new BizException(ErrorCode.BAD_REQUEST, "note 过长");
            }
        }
        reportRepositoryPort.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "举报不存在"));
        reportRepositoryPort.updateStatus(id, status, handlerId, LocalDateTime.now(), finalNote);
    }

    /**
     * 校验创建命令。
     *
     * @param command 创建命令
     */
    private static void validate(ReportCreateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.reporterId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "reporterId 不合法");
        }
        if (command.targetType() == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "targetType 不能为空");
        }
        if (command.targetId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "targetId 不合法");
        }
        if (!StringUtils.hasText(command.reason())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "reason 不能为空");
        }
        if (command.reason().trim().length() > 1024) {
            throw new BizException(ErrorCode.BAD_REQUEST, "reason 过长");
        }
    }
}

