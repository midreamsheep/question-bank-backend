package indi.midreamsheep.vegetable.backend.features.file.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import indi.midreamsheep.vegetable.backend.features.file.domain.command.FileObjectCreateCommand;
import indi.midreamsheep.vegetable.backend.features.file.domain.model.FileObjectData;
import indi.midreamsheep.vegetable.backend.features.file.domain.port.FileObjectRepositoryPort;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.entity.FileObjectEntity;
import indi.midreamsheep.vegetable.backend.infrastructure.database.mysql.mapper.FileObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 文件对象仓储适配器：使用 MyBatis-Plus 将元数据落库到 vf_file_object。
 */
@Component
public class FileObjectRepositoryAdapter implements FileObjectRepositoryPort {

    private final FileObjectMapper fileObjectMapper;

    /**
     * 构造文件对象仓储适配器。
     *
     * @param fileObjectMapper 文件对象 Mapper
     */
    public FileObjectRepositoryAdapter(FileObjectMapper fileObjectMapper) {
        this.fileObjectMapper = fileObjectMapper;
    }

    /**
     * 创建文件对象记录并返回ID。
     *
     * @param command 创建命令
     * @return 文件ID
     */
    @Override
    public long create(FileObjectCreateCommand command) {
        LocalDateTime now = LocalDateTime.now();
        FileObjectEntity entity = new FileObjectEntity();
        entity.setShareKey(command.shareKey());
        entity.setObjectKey(command.objectKey());
        entity.setOriginalFilename(command.originalFilename());
        entity.setContentType(command.contentType());
        entity.setSize(command.size());
        entity.setUploaderId(command.uploaderId());
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setDeleted(0);
        fileObjectMapper.insert(entity);
        return entity.getId() == null ? 0L : entity.getId();
    }

    /**
     * 按 ID 查找文件对象。
     *
     * @param id 文件ID
     * @return 文件对象（可为空）
     */
    @Override
    public Optional<FileObjectData> findById(long id) {
        QueryWrapper<FileObjectEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id).eq("deleted", 0);
        return Optional.ofNullable(fileObjectMapper.selectOne(wrapper)).map(FileObjectRepositoryAdapter::toData);
    }

    /**
     * 按 shareKey 查找文件对象。
     *
     * @param shareKey 分享 key
     * @return 文件对象（可为空）
     */
    @Override
    public Optional<FileObjectData> findByShareKey(String shareKey) {
        QueryWrapper<FileObjectEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("share_key", shareKey).eq("deleted", 0);
        return Optional.ofNullable(fileObjectMapper.selectOne(wrapper)).map(FileObjectRepositoryAdapter::toData);
    }

    /**
     * 转换为领域数据。
     *
     * @param entity 实体
     * @return 领域数据
     */
    private static FileObjectData toData(FileObjectEntity entity) {
        return new FileObjectData(
                entity.getId() == null ? 0L : entity.getId(),
                entity.getShareKey(),
                entity.getObjectKey(),
                entity.getOriginalFilename(),
                entity.getSize() == null ? 0L : entity.getSize(),
                entity.getContentType(),
                entity.getUploaderId(),
                entity.getCreatedAt()
        );
    }
}
