package indi.midreamsheep.vegetable.backend.features.problemtype.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.command.ProblemTypeCreateCommand;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.command.ProblemTypeUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.model.ProblemTypeData;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.port.ProblemTypeRepositoryPort;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.ProblemTypeEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.ProblemTypeMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 题型仓储适配器：通过 MyBatis-Plus 完成持久化。
 */
@Component
public class ProblemTypeRepositoryAdapter implements ProblemTypeRepositoryPort {

    private final ProblemTypeMapper problemTypeMapper;

    /**
     * 构造题型仓储适配器。
     *
     * @param problemTypeMapper 题型 Mapper
     */
    public ProblemTypeRepositoryAdapter(ProblemTypeMapper problemTypeMapper) {
        this.problemTypeMapper = problemTypeMapper;
    }

    @Override
    public long create(ProblemTypeCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        ProblemTypeEntity entity = new ProblemTypeEntity();
        entity.setSubject(command.subject());
        entity.setName(command.name());
        entity.setDescription(command.description());
        entity.setSortOrder(command.sortOrder());
        entity.setEnabled(command.enabled() ? 1 : 0);
        entity.setDeleted(0);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        problemTypeMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public ProblemTypeData update(ProblemTypeUpdateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        UpdateWrapper<ProblemTypeEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", command.id())
                .set("subject", command.subject())
                .set("name", command.name())
                .set("description", command.description())
                .set("sort_order", command.sortOrder())
                .set("enabled", command.enabled() ? 1 : 0)
                .set("updated_at", now);
        problemTypeMapper.update(null, wrapper);
        return findById(command.id()).orElseThrow(() -> new IllegalStateException("题型更新失败"));
    }

    @Override
    public Optional<ProblemTypeData> findById(long id) {
        QueryWrapper<ProblemTypeEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id)
                .eq("deleted", 0);
        return Optional.ofNullable(problemTypeMapper.selectOne(wrapper))
                .map(ProblemTypeRepositoryAdapter::toData);
    }

    @Override
    public List<ProblemTypeData> list(String subject) {
        QueryWrapper<ProblemTypeEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0)
                .eq("enabled", 1);
        if (subject != null) {
            wrapper.eq("subject", subject);
        }
        wrapper.orderByAsc("sort_order").orderByAsc("id");
        return problemTypeMapper.selectList(wrapper).stream()
                .map(ProblemTypeRepositoryAdapter::toData)
                .toList();
    }

    /**
     * 将实体转换为领域模型。
     *
     * @param entity 题型实体
     * @return 题型数据
     */
    private static ProblemTypeData toData(ProblemTypeEntity entity) {
        return new ProblemTypeData(
                entity.getId(),
                entity.getSubject(),
                entity.getName(),
                entity.getDescription(),
                entity.getSortOrder() == null ? 0 : entity.getSortOrder(),
                entity.getEnabled() != null && entity.getEnabled() == 1
        );
    }
}
