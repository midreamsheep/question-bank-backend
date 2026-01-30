package indi.midreamsheep.vegetable.backend.features.file.domain.port;

import indi.midreamsheep.vegetable.backend.features.file.domain.command.FileObjectCreateCommand;
import indi.midreamsheep.vegetable.backend.features.file.domain.model.FileObjectData;

import java.util.Optional;

/**
 * 文件对象仓储端口：用于将文件元数据持久化到数据库。
 */
public interface FileObjectRepositoryPort {

    /**
     * 创建文件对象记录并返回ID。
     *
     * @param command 创建命令
     * @return 文件ID
     */
    long create(FileObjectCreateCommand command);

    /**
     * 按 ID 查找文件对象。
     *
     * @param id 文件ID
     * @return 文件对象（可为空）
     */
    Optional<FileObjectData> findById(long id);

    /**
     * 按 shareKey 查找文件对象。
     *
     * @param shareKey 分享 key
     * @return 文件对象（可为空）
     */
    Optional<FileObjectData> findByShareKey(String shareKey);
}
