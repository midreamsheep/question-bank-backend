package indi.midreamsheep.vegetable.backend.features.tag.domain.port;

import indi.midreamsheep.vegetable.backend.features.tag.domain.command.TagCreateCommand;
import indi.midreamsheep.vegetable.backend.features.tag.domain.model.TagData;

import java.util.List;
import java.util.Optional;

/**
 * 标签仓储端口。
 */
public interface TagRepositoryPort {

    /**
     * 创建标签。
     *
     * @param command 创建命令
     * @return 标签ID
     */
    long create(TagCreateCommand command);

    /**
     * 获取标签列表。
     *
     * @param subject 学科
     * @param keyword 关键字
     * @return 标签列表
     */
    List<TagData> list(String subject, String keyword);

    /**
     * 根据 ID 列表查询标签。
     *
     * @param ids ID列表
     * @return 标签列表
     */
    List<TagData> findByIds(List<Long> ids);

    /**
     * 根据学科与名称查询标签（用于避免重复创建）。
     *
     * @param subject 学科
     * @param name 标签名（trim 后）
     * @return 标签数据
     */
    Optional<TagData> findBySubjectAndName(String subject, String name);
}
