package indi.midreamsheep.vegetable.backend.features.tag.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import indi.midreamsheep.vegetable.backend.features.tag.domain.command.TagCreateCommand;
import indi.midreamsheep.vegetable.backend.features.tag.domain.model.TagData;
import indi.midreamsheep.vegetable.backend.features.tag.domain.port.TagRepositoryPort;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.TagEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.TagMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 标签仓储适配器：通过 MyBatis-Plus 完成持久化。
 */
@Component
public class TagRepositoryAdapter implements TagRepositoryPort {

    private final TagMapper tagMapper;

    /**
     * 构造标签仓储适配器。
     *
     * @param tagMapper 标签 Mapper
     */
    public TagRepositoryAdapter(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @Override
    public long create(TagCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        TagEntity entity = new TagEntity();
        entity.setSubject(command.subject());
        entity.setName(command.name());
        entity.setDeleted(0);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        tagMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public List<TagData> list(String subject, String keyword) {
        QueryWrapper<TagEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0);
        if (subject != null) {
            wrapper.eq("subject", subject);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like("name", keyword.trim());
        }
        wrapper.orderByAsc("name");
        return tagMapper.selectList(wrapper).stream()
                .map(TagRepositoryAdapter::toData)
                .toList();
    }

    @Override
    public List<TagData> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<Long> distinctIds = ids.stream().filter(id -> id != null && id > 0).distinct().toList();
        if (distinctIds.isEmpty()) {
            return List.of();
        }
        QueryWrapper<TagEntity> wrapper = new QueryWrapper<>();
        wrapper.in("id", distinctIds)
                .eq("deleted", 0);
        return tagMapper.selectList(wrapper).stream()
                .map(TagRepositoryAdapter::toData)
                .toList();
    }

    @Override
    public Optional<TagData> findBySubjectAndName(String subject, String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        QueryWrapper<TagEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", 0)
                .eq("name", name.trim());
        if (subject != null) {
            wrapper.eq("subject", subject);
        } else {
            wrapper.isNull("subject");
        }
        return Optional.ofNullable(tagMapper.selectOne(wrapper))
                .map(TagRepositoryAdapter::toData);
    }

    /**
     * 将实体转换为领域模型。
     *
     * @param entity 标签实体
     * @return 标签数据
     */
    private static TagData toData(TagEntity entity) {
        return new TagData(
                entity.getId(),
                entity.getSubject(),
                entity.getName()
        );
    }
}
