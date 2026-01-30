package indi.midreamsheep.vegetable.backend.features.problemtype.domain.port;

import indi.midreamsheep.vegetable.backend.features.problemtype.domain.command.ProblemTypeCreateCommand;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.command.ProblemTypeUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.problemtype.domain.model.ProblemTypeData;

import java.util.List;
import java.util.Optional;

/**
 * 题型仓储端口。
 */
public interface ProblemTypeRepositoryPort {

    /**
     * 创建题型。
     *
     * @param command 创建命令
     * @return 题型ID
     */
    long create(ProblemTypeCreateCommand command);

    /**
     * 更新题型。
     *
     * @param command 更新命令
     * @return 更新后的题型
     */
    ProblemTypeData update(ProblemTypeUpdateCommand command);

    /**
     * 获取题型。
     *
     * @param id 题型ID
     * @return 题型数据
     */
    Optional<ProblemTypeData> findById(long id);

    /**
     * 获取题型列表。
     *
     * @param subject 学科
     * @return 题型列表
     */
    List<ProblemTypeData> list(String subject);
}
