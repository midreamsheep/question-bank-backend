package indi.midreamsheep.vegetable.backend.features.role.domain;

import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.role.domain.command.RoleCreateCommand;
import indi.midreamsheep.vegetable.backend.features.role.domain.command.RoleUpdateCommand;
import indi.midreamsheep.vegetable.backend.features.role.domain.model.RoleData;
import indi.midreamsheep.vegetable.backend.features.role.domain.port.RoleRepositoryPort;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;

/**
 * 角色领域服务。
 */
public class RoleDomainService {

    private final RoleRepositoryPort roleRepositoryPort;

    /**
     * 构造角色领域服务。
     *
     * @param roleRepositoryPort 角色仓储端口
     */
    public RoleDomainService(RoleRepositoryPort roleRepositoryPort) {
        this.roleRepositoryPort = roleRepositoryPort;
    }

    /**
     * 获取角色列表。
     *
     * @return 角色列表
     */
    public List<RoleData> list() {
        return roleRepositoryPort.list();
    }

    /**
     * 创建角色。
     *
     * @param command 创建命令
     * @return 角色ID
     */
    public long create(RoleCreateCommand command) {
        validate(command);
        String code = normalizeCode(command.code());
        roleRepositoryPort.findByCode(code).ifPresent(existing -> {
            throw new BizException(ErrorCode.BAD_REQUEST, "角色编码已存在");
        });
        return roleRepositoryPort.create(new RoleCreateCommand(code, command.name().trim()));
    }

    /**
     * 更新角色。
     *
     * @param command 更新命令
     * @return 更新后的角色
     */
    public RoleData update(RoleUpdateCommand command) {
        validate(command);
        roleRepositoryPort.findById(command.id())
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "角色不存在"));
        return roleRepositoryPort.update(new RoleUpdateCommand(command.id(), command.name().trim()));
    }

    /**
     * 删除角色。
     *
     * @param id 角色ID
     */
    public void delete(long id) {
        if (id <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        roleRepositoryPort.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "角色不存在"));
        roleRepositoryPort.softDelete(id);
    }

    /**
     * 规范化角色编码。
     *
     * @param code 角色编码
     * @return 规范化后的编码
     */
    private static String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * 校验创建命令。
     *
     * @param command 创建命令
     */
    private static void validate(RoleCreateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (!StringUtils.hasText(command.code())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "code 不能为空");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "name 不能为空");
        }
        if (command.code().trim().length() > 32) {
            throw new BizException(ErrorCode.BAD_REQUEST, "code 过长");
        }
        if (command.name().trim().length() > 64) {
            throw new BizException(ErrorCode.BAD_REQUEST, "name 过长");
        }
    }

    /**
     * 校验更新命令。
     *
     * @param command 更新命令
     */
    private static void validate(RoleUpdateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (command.id() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "id 不合法");
        }
        if (!StringUtils.hasText(command.name())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "name 不能为空");
        }
        if (command.name().trim().length() > 64) {
            throw new BizException(ErrorCode.BAD_REQUEST, "name 过长");
        }
    }
}
