package indi.midreamsheep.vegetable.backend.features.report.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.report.domain.ReportStatus;
import indi.midreamsheep.vegetable.backend.features.report.domain.ReportTargetType;
import indi.midreamsheep.vegetable.backend.features.report.domain.command.ReportCreateCommand;
import indi.midreamsheep.vegetable.backend.features.report.domain.model.ReportData;
import indi.midreamsheep.vegetable.backend.features.report.domain.port.ReportRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.report.domain.query.ReportQuery;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.ReportEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.ReportMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 举报仓储适配器：通过 MyBatis-Plus 完成持久化。
 */
@Component
public class ReportRepositoryAdapter implements ReportRepositoryPort {

    private final ReportMapper reportMapper;

    /**
     * 构造举报仓储适配器。
     *
     * @param reportMapper 举报 Mapper
     */
    public ReportRepositoryAdapter(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    /**
     * 创建举报。
     *
     * @param command 创建命令
     * @return 举报ID
     */
    @Override
    public long create(ReportCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        ReportEntity entity = new ReportEntity();
        entity.setReporterId(command.reporterId());
        entity.setTargetType(command.targetType().name());
        entity.setTargetId(command.targetId());
        entity.setReason(command.reason());
        entity.setStatus(ReportStatus.OPEN.name());
        entity.setHandlerId(null);
        entity.setHandledAt(null);
        entity.setHandlingNote(null);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setDeleted(0);
        reportMapper.insert(entity);
        return entity.getId();
    }

    /**
     * 举报列表（分页）。
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Override
    public PageResponse<ReportData> list(ReportQuery query) {
        QueryWrapper<ReportEntity> countWrapper = buildListWrapper(query);
        long total = reportMapper.selectCount(countWrapper);

        QueryWrapper<ReportEntity> listWrapper = buildListWrapper(query);
        listWrapper.orderByDesc("created_at").orderByDesc("id");
        int offset = Math.max(0, (query.page() - 1) * query.pageSize());
        listWrapper.last("limit " + offset + ", " + query.pageSize());
        List<ReportData> items = reportMapper.selectList(listWrapper).stream()
                .map(ReportRepositoryAdapter::toData)
                .toList();
        return new PageResponse<>(items, query.page(), query.pageSize(), total);
    }

    /**
     * 根据 ID 查询举报。
     *
     * @param id 举报ID
     * @return 举报（可为空）
     */
    @Override
    public Optional<ReportData> findById(long id) {
        QueryWrapper<ReportEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id).eq("deleted", 0);
        return Optional.ofNullable(reportMapper.selectOne(wrapper)).map(ReportRepositoryAdapter::toData);
    }

    /**
     * 更新举报状态。
     *
     * @param id 举报ID
     * @param status 状态
     * @param handlerId 处理人用户ID
     * @param handledAt 处理时间
     * @param handlingNote 处理备注
     */
    @Override
    public void updateStatus(long id, ReportStatus status, long handlerId, LocalDateTime handledAt, String handlingNote) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<ReportEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0)
                .set("status", status.name())
                .set("handler_id", handlerId)
                .set("handled_at", handledAt)
                .set("handling_note", handlingNote)
                .set("updated_at", now);
        reportMapper.update(null, wrapper);
    }

    /**
     * 构造列表查询条件。
     *
     * @param query 查询条件
     * @return 查询包装器
     */
    private static QueryWrapper<ReportEntity> buildListWrapper(ReportQuery query) {
        QueryWrapper<ReportEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (query.targetType() != null) {
            wrapper.eq("target_type", query.targetType().name());
        }
        if (query.status() != null) {
            wrapper.eq("status", query.status().name());
        }
        return wrapper;
    }

    /**
     * 转换为领域数据。
     *
     * @param entity 实体
     * @return 领域数据
     */
    private static ReportData toData(ReportEntity entity) {
        ReportTargetType targetType = entity.getTargetType() == null ? null : ReportTargetType.valueOf(entity.getTargetType());
        ReportStatus status = entity.getStatus() == null ? null : ReportStatus.valueOf(entity.getStatus());
        return new ReportData(
                entity.getId() == null ? 0L : entity.getId(),
                entity.getReporterId() == null ? 0L : entity.getReporterId(),
                targetType,
                entity.getTargetId() == null ? 0L : entity.getTargetId(),
                entity.getReason(),
                status,
                entity.getHandlerId(),
                entity.getHandledAt(),
                entity.getHandlingNote(),
                entity.getCreatedAt()
        );
    }
}

