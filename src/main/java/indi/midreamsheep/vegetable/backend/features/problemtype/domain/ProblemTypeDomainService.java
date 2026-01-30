package indi.midreamsheep.vegetable.backend.features.problemtype.domain;

import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.command.ProblemTypeCreateCommand;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.command.ProblemTypeUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.model.ProblemTypeData;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.port.ProblemTypeRepositoryPort;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 题型领域服务。
 */
public class ProblemTypeDomainService {

    private final ProblemTypeRepositoryPort problemTypeRepositoryPort;

    /**
     * 构造题型领域服务。
     *
     * @param problemTypeRepositoryPort 题型仓储端口
     */
    public ProblemTypeDomainService(ProblemTypeRepositoryPort problemTypeRepositoryPort) {
        this.problemTypeRepositoryPort = problemTypeRepositoryPort;
    }

    /**
     * 获取题型列表。
     *
     * @param subject 学科
     * @return 题型列表
     */
    public List<ProblemTypeData> list(String subject) {
        return problemTypeRepositoryPort.list(subject);
    }

    /**
     * 创建题型。
     *
     * @param command 创建命令
     * @return 题型ID
     */
    public long create(ProblemTypeCreateCommand command) {
        validate(command);
        return problemTypeRepositoryPort.create(command);
    }

    /**
     * 更新题型。
     *
     * @param command 更新命令
     * @return 更新后的题型
     */
    public ProblemTypeData update(ProblemTypeUpdateCommand command) {
        validate(command);
        problemTypeRepositoryPort.findById(command.id())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "题型不存在"));
        return problemTypeRepositoryPort.update(command);
    }

    /**
     * 校验创建命令。
     *
     * @param command 创建命令
     */
    private static void validate(ProblemTypeCreateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "题型名称不能为空");
        }
        if (command.sortOrder() < 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "sortOrder 不能小于 0");
        }
    }

    /**
     * 校验更新命令。
     *
     * @param command 更新命令
     */
    private static void validate(ProblemTypeUpdateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.id() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "题型名称不能为空");
        }
        if (command.sortOrder() < 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "sortOrder 不能小于 0");
        }
    }
}
