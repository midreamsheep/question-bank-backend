package indi.midreamsheep.vegetable.backend.features.file.domain;

import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.file.domain.command.FileObjectCreateCommand;
import indi.midreamsheep.vegetable.backend.features.file.domain.model.FileDownloadData;
import indi.midreamsheep.vegetable.backend.features.file.domain.model.FileObjectData;
import indi.midreamsheep.vegetable.backend.features.file.domain.port.FileObjectRepositoryPort;
import indi.midreamsheep.vegetable.backend.features.file.domain.port.FileStoragePort;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.UUID;

/**
 * 文件领域服务：封装文件上传与访问链接生成等用例。
 */
public class FileDomainService {

    private final FileStoragePort fileStoragePort;
    private final FileObjectRepositoryPort fileObjectRepositoryPort;

    /**
     * 构造文件领域服务。
     *
     * @param fileStoragePort 文件存储端口
     * @param fileObjectRepositoryPort 文件对象仓储端口
     */
    public FileDomainService(FileStoragePort fileStoragePort, FileObjectRepositoryPort fileObjectRepositoryPort) {
        this.fileStoragePort = fileStoragePort;
        this.fileObjectRepositoryPort = fileObjectRepositoryPort;
    }

    /**
     * 上传文件。
     *
     * @param objectKey 对象 key
     * @param inputStream 文件流
     * @param size 文件大小（字节）
     * @param contentType 文件类型（MIME）
     * @return 已存储文件
     */
    public StoredFile upload(String objectKey, InputStream inputStream, long size, String contentType) {
        return fileStoragePort.upload(objectKey, inputStream, size, contentType);
    }

    /**
     * 创建文件对象记录（元数据落库）。
     *
     * @param command 创建命令
     * @return 文件ID
     */
    public long createFileObject(FileObjectCreateCommand command) {
        validate(command);
        return fileObjectRepositoryPort.create(command);
    }

    /**
     * 打开文件下载流（通过 shareKey）。
     *
     * @param shareKey 分享 key
     * @return 下载数据（包含元数据与输入流）
     */
    public FileDownloadData openDownloadByShareKey(String shareKey) {
        if (!StringUtils.hasText(shareKey)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "shareKey 不能为空");
        }
        FileObjectData data = fileObjectRepositoryPort.findByShareKey(shareKey)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "文件不存在"));
        InputStream inputStream = fileStoragePort.openStream(data.objectKey());
        return new FileDownloadData(data, inputStream);
    }

    /**
     * 通过文件ID生成下载用的预签名 URL。
     *
     * @param fileId 文件ID
     * @param expiresSeconds 有效期（秒）
     * @return 预签名 URL
     */
    public String presignedGetUrlByFileId(long fileId, int expiresSeconds) {
        if (fileId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "fileId 不合法");
        }
        FileObjectData data = fileObjectRepositoryPort.findById(fileId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "文件不存在"));
        if (!StringUtils.hasText(data.objectKey())) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "文件元数据异常");
        }
        return fileStoragePort.presignedGetUrl(data.objectKey(), expiresSeconds);
    }

    /**
     * 获取文件对象信息。
     *
     * @param fileId 文件ID
     * @return 文件对象信息
     */
    public FileObjectData getFileObject(long fileId) {
        if (fileId <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "fileId 不合法");
        }
        return fileObjectRepositoryPort.findById(fileId)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "文件不存在"));
    }

    /**
     * 生成下载用的预签名 URL。
     *
     * @param objectKey 对象 key
     * @param expiresSeconds 有效期（秒）
     * @return 预签名 URL
     */
    public String presignedGetUrl(String objectKey, int expiresSeconds) {
        return fileStoragePort.presignedGetUrl(objectKey, expiresSeconds);
    }

    /**
     * 校验创建命令。
     *
     * @param command 创建命令
     */
    private static void validate(FileObjectCreateCommand command) {
        if (command == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }
        if (!StringUtils.hasText(command.shareKey())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "shareKey 不能为空");
        }
        if (!StringUtils.hasText(command.objectKey())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "objectKey 不能为空");
        }
        if (command.size() < 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "size 不合法");
        }
        if (command.uploaderId() != null && command.uploaderId() <= 0) {
            throw new BizException(ErrorCode.BAD_REQUEST, "uploaderId 不合法");
        }
    }

    /**
     * 生成 shareKey。
     *
     * @return shareKey
     */
    public static String generateShareKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
