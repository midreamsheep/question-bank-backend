package indi.midreamsheep.vegetable.backend.features.tag.domain;

import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.tag.domain.command.TagCreateCommand;
import indi.midreamsheep.vegetable.backend.features.tag.domain.model.TagData;
import indi.midreamsheep.vegetable.backend.features.tag.domain.port.TagRepositoryPort;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 标签领域服务。
 */
public class TagDomainService {

    private final TagRepositoryPort tagRepositoryPort;

    /**
     * 构造标签领域服务。
     *
     * @param tagRepositoryPort 标签仓储端口
     */
    public TagDomainService(TagRepositoryPort tagRepositoryPort) {
        this.tagRepositoryPort = tagRepositoryPort;
    }

    /**
     * 获取标签列表。
     *
     * @param subject 学科
     * @param keyword 关键字
     * @return 标签列表
     */
    public List<TagData> list(String subject, String keyword) {
        return tagRepositoryPort.list(subject, keyword);
    }

    /**
     * 创建标签。
     *
     * @param command 创建命令
     * @return 标签ID
     */
    public long create(TagCreateCommand command) {
        validate(command);
        return tagRepositoryPort.create(command);
    }

    /**
     * 获取或创建标签（用于“发布时新增标签”等场景）。
     *
     * @param command 创建命令
     * @return 标签数据
     */
    public TagData getOrCreate(TagCreateCommand command) {
        validate(command);
        String name = command.name().trim();
        return tagRepositoryPort.findBySubjectAndName(command.subject(), name)
                .orElseGet(() -> {
                    long id = tagRepositoryPort.create(new TagCreateCommand(command.subject(), name));
                    return new TagData(id, command.subject(), name);
                });
    }

    /**
     * 校验创建命令。
     *
     * @param command 创建命令
     */
    private static void validate(TagCreateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "标签名称不能为空");
        }
        if (command.name().trim().length() > 64) {
            throw new BizException(ErrorCode.BAD_REQUEST, "标签名称过长");
        }
    }
}
