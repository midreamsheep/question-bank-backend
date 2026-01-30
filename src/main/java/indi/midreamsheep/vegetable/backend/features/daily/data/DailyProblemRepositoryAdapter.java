package indi.midreamsheep.vegetable.backend.features.daily.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import indi.midreamsheep.vegetable.backend.common.api.PageResponse;
import indi.midreamsheep.vegetable.backend.features.daily.domain.DailyProblemStatus;
import indi.midreamsheep.vegetable.backend.features.daily.domain.model.DailyProblemData;
import indi.midreamsheep.vegetable.backend.features.daily.domain.port.DailyProblemRepositoryPort;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.DailyProblemEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.DailyProblemMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 每日一题仓储适配器：通过 MyBatis-Plus 完成持久化。
 */
@Component
public class DailyProblemRepositoryAdapter implements DailyProblemRepositoryPort {

    private final DailyProblemMapper dailyProblemMapper;

    /**
     * 构造每日一题仓储适配器。
     *
     * @param dailyProblemMapper 每日一题 Mapper
     */
    public DailyProblemRepositoryAdapter(DailyProblemMapper dailyProblemMapper) {
        this.dailyProblemMapper = dailyProblemMapper;
    }

    @Override
    public List<DailyProblemData> findByDay(LocalDate day) {
        QueryWrapper<DailyProblemEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("day", day)
                .eq("deleted", 0);
        wrapper.orderByDesc("published_at").orderByDesc("id");
        return dailyProblemMapper.selectList(wrapper).stream()
                .map(DailyProblemRepositoryAdapter::toData)
                .toList();
    }

    @Override
    public PageResponse<DailyProblemData> list(LocalDate from, LocalDate to, int page, int pageSize) {
        QueryWrapper<DailyProblemEntity> countWrapper = buildListWrapper(from, to);
        long total = dailyProblemMapper.selectCount(countWrapper);

        QueryWrapper<DailyProblemEntity> listWrapper = buildListWrapper(from, to);
        listWrapper.orderByDesc("day").orderByDesc("published_at").orderByDesc("id");
        int offset = Math.max(0, (page - 1) * pageSize);
        listWrapper.last("limit " + offset + ", " + pageSize);
        List<DailyProblemData> items = dailyProblemMapper.selectList(listWrapper).stream()
                .map(DailyProblemRepositoryAdapter::toData)
                .toList();
        return new PageResponse<>(items, page, pageSize, total);
    }

    @Override
    public DailyProblemData publish(DailyProblemData data) {
        LocalDateTime now = LocalDateTime.now();
        DailyProblemEntity existing = findEntityByDayAndProblemId(data.day(), data.problemId());
        if (existing == null) {
            DailyProblemEntity entity = new DailyProblemEntity();
            entity.setDay(data.day());
            entity.setProblemId(data.problemId());
            entity.setStatus(DailyProblemStatus.PUBLISHED.name());
            entity.setCopywriting(data.copywriting());
            entity.setOperatorId(data.operatorId());
            entity.setPublishedAt(now);
            entity.setRevokedAt(null);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            entity.setDeleted(0);
            dailyProblemMapper.insert(entity);
            return toData(entity);
        }
        UpdateWrapper<DailyProblemEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", existing.getId())
                .set("problem_id", data.problemId())
                .set("status", DailyProblemStatus.PUBLISHED.name())
                .set("copywriting", data.copywriting())
                .set("operator_id", data.operatorId())
                .set("published_at", now)
                .set("revoked_at", null)
                .set("updated_at", now);
        dailyProblemMapper.update(null, wrapper);
        return findById(existing.getId()).orElseThrow(() -> new IllegalStateException("每日一题发布失败"));
    }

    @Override
    public List<DailyProblemData> revoke(LocalDate day, long operatorId) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<DailyProblemEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("day", day)
                .eq("deleted", 0)
                .set("status", DailyProblemStatus.REVOKED.name())
                .set("operator_id", operatorId)
                .set("revoked_at", now)
                .set("updated_at", now);
        int updated = dailyProblemMapper.update(null, wrapper);
        if (updated <= 0) {
            throw new IllegalStateException("每日一题不存在");
        }
        return findByDay(day).stream()
                .filter(item -> item.status() == DailyProblemStatus.REVOKED)
                .toList();
    }

    @Override
    public DailyProblemData revokeById(long id, long operatorId) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<DailyProblemEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0)
                .set("status", DailyProblemStatus.REVOKED.name())
                .set("operator_id", operatorId)
                .set("revoked_at", now)
                .set("updated_at", now);
        int updated = dailyProblemMapper.update(null, wrapper);
        if (updated <= 0) {
            throw new IllegalStateException("每日一题不存在");
        }
        return findById(id).orElseThrow(() -> new IllegalStateException("每日一题撤回失败"));
    }

    /**
     * 根据日期与题目ID查询实体。
     *
     * @param day 日期
     * @param problemId 题目ID
     * @return 实体或 null
     */
    private DailyProblemEntity findEntityByDayAndProblemId(LocalDate day, long problemId) {
        QueryWrapper<DailyProblemEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("day", day)
                .eq("problem_id", problemId)
                .eq("deleted", 0);
        return dailyProblemMapper.selectOne(wrapper);
    }

    /**
     * 根据 ID 查询数据。
     *
     * @param id ID
     * @return 数据或空
     */
    private Optional<DailyProblemData> findById(long id) {
        QueryWrapper<DailyProblemEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id).eq("deleted", 0);
        return Optional.ofNullable(dailyProblemMapper.selectOne(wrapper))
                .map(DailyProblemRepositoryAdapter::toData);
    }

    /**
     * 将实体转换为领域数据。
     *
     * @param entity 实体
     * @return 领域数据
     */
    private static DailyProblemData toData(DailyProblemEntity entity) {
        DailyProblemStatus status = entity.getStatus() == null ? null : DailyProblemStatus.valueOf(entity.getStatus());
        return new DailyProblemData(
                entity.getId() == null ? 0L : entity.getId(),
                entity.getDay(),
                entity.getProblemId() == null ? 0L : entity.getProblemId(),
                status,
                entity.getCopywriting(),
                entity.getOperatorId() == null ? 0L : entity.getOperatorId(),
                entity.getPublishedAt(),
                entity.getRevokedAt()
        );
    }

    /**
     * 构造列表查询条件。
     *
     * @param from 起始日期
     * @param to 截止日期
     * @return 查询包装器
     */
    private static QueryWrapper<DailyProblemEntity> buildListWrapper(LocalDate from, LocalDate to) {
        QueryWrapper<DailyProblemEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0)
                .eq("status", DailyProblemStatus.PUBLISHED.name());
        if (from != null) {
            wrapper.ge("day", from);
        }
        if (to != null) {
            wrapper.le("day", to);
        }
        return wrapper;
    }
}
