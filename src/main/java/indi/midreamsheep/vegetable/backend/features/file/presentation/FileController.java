package indi.midreamsheep.vegetable.backend.features.file.presentation;

import indi.midreamsheep.vegetable.backend.common.api.ApiResponse;
import indi.midreamsheep.vegetable.backend.common.error.BizException;
import indi.midreamsheep.vegetable.backend.common.error.ErrorCode;
import indi.midreamsheep.vegetable.backend.features.file.domain.FileDomainService;
import indi.midreamsheep.vegetable.backend.features.file.domain.StoredFile;
import indi.midreamsheep.vegetable.backend.features.file.domain.command.FileObjectCreateCommand;
import indi.midreamsheep.vegetable.backend.features.file.domain.model.FileDownloadData;
import indi.midreamsheep.vegetable.backend.features.file.presentation.dto.FileUploadResponse;
import indi.midreamsheep.vegetable.backend.features.file.presentation.dto.FileShareResponse;
import indi.midreamsheep.vegetable.backend.features.file.presentation.dto.PresignedUrlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件相关接口（上传、获取下载链接等）。
 */
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    /**
     * 预签名 URL 默认有效期（秒）。
     */
    public static final int DEFAULT_PRESIGN_EXPIRE_SECONDS = 3600;

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private final FileDomainService fileDomainService;

    /**
     * 构造文件控制器。
     *
     * @param fileDomainService 文件领域服务
     */
    public FileController(FileDomainService fileDomainService) {
        this.fileDomainService = fileDomainService;
    }

    /**
     * 上传文件（multipart）。
     *
     * @param file 上传文件
     * @return 统一响应体（包含 objectKey 等信息）
     * @throws IOException IO 异常
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileUploadResponse> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BizException(ErrorCode.BAD_REQUEST, "文件不能为空");
        }

        String userId = currentUserId();
        String originalFilename = file.getOriginalFilename();
        String safeFilename = sanitizeFilename(originalFilename);
        String objectKey = buildObjectKey(userId, safeFilename);

        try (InputStream inputStream = file.getInputStream()) {
            String contentType = file.getContentType();
            if (!StringUtils.hasText(contentType)) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            StoredFile stored = fileDomainService.upload(objectKey, inputStream, file.getSize(), contentType);
            Long uploaderId = parseLongOrNull(userId);
            String shareKey = FileDomainService.generateShareKey();
            long fileId = fileDomainService.createFileObject(new FileObjectCreateCommand(
                    shareKey,
                    stored.objectKey(),
                    originalFilename,
                    stored.size(),
                    stored.contentType(),
                    uploaderId
            ));
            log.info("event=file_upload_success userId={} objectKey={} size={}", userId, stored.objectKey(), stored.size());
            return ApiResponse.ok(new FileUploadResponse(
                    fileId,
                    shareKey,
                    "/api/v1/files/share/" + shareKey,
                    stored.objectKey(),
                    originalFilename,
                    stored.size(),
                    stored.contentType()
            ));
        }
    }

    /**
     * 永久链接下载（通过 shareKey，无需登录）。
     *
     * @param shareKey 分享 key
     * @return 文件流响应
     */
    @GetMapping("/share/{shareKey}")
    public ResponseEntity<StreamingResponseBody> downloadByShareKey(@PathVariable("shareKey") String shareKey) {
        if (!StringUtils.hasText(shareKey)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "shareKey 不能为空");
        }
        FileDownloadData download = fileDomainService.openDownloadByShareKey(shareKey);
        String filename = sanitizeFilename(download.file().originalFilename());
        String contentType = download.file().contentType();
        MediaType mediaType = StringUtils.hasText(contentType)
                ? MediaType.parseMediaType(contentType)
                : MediaType.APPLICATION_OCTET_STREAM;

        StreamingResponseBody body = outputStream -> {
            try (InputStream in = download.inputStream()) {
                in.transferTo(outputStream);
            }
        };

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
                .body(body);
    }

    /**
     * 通过 fileId 获取永久链接信息（用于将 avatarFileId 等业务字段转换为可展示的图片 URL）。
     *
     * @param fileId 文件ID
     * @return 分享信息（包含 shareUrl）
     */
    @GetMapping("/share-url")
    public ApiResponse<FileShareResponse> shareUrl(@RequestParam("fileId") long fileId) {
        String shareKey = fileDomainService.getFileObject(fileId).shareKey();
        return ApiResponse.ok(new FileShareResponse(shareKey, "/api/v1/files/share/" + shareKey));
    }

    /**
     * 获取下载用的预签名 URL。
     *
     * @param objectKey 对象 key
     * @param expiresSeconds 有效期（秒），为空则使用默认值
     * @return 统一响应体（包含预签名 URL）
     */
    @GetMapping("/presigned-get-url")
    public ApiResponse<PresignedUrlResponse> presignedGetUrl(
            @RequestParam(value = "fileId", required = false) Long fileId,
            @RequestParam(value = "objectKey", required = false) String objectKey,
            @RequestParam(value = "expiresSeconds", required = false) Integer expiresSeconds
    ) {
        if ((fileId == null || fileId <= 0) && !StringUtils.hasText(objectKey)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "fileId 或 objectKey 必须提供一个");
        }
        int expiry = (expiresSeconds == null ? DEFAULT_PRESIGN_EXPIRE_SECONDS : expiresSeconds);
        if (expiry < 60) {
            throw new BizException(ErrorCode.BAD_REQUEST, "expiresSeconds 最小为 60");
        }
        String url = (fileId != null && fileId > 0)
                ? fileDomainService.presignedGetUrlByFileId(fileId, expiry)
                : fileDomainService.presignedGetUrl(objectKey, expiry);
        return ApiResponse.ok(new PresignedUrlResponse(url));
    }

    /**
     * 获取当前用户标识（由 Spring Security 注入）。
     *
     * @return 用户标识
     */
    private static String currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return "anonymous";
        }
        return String.valueOf(authentication.getPrincipal());
    }

    /**
     * 构建对象 key（建议按用户与日期分目录，便于管理）。
     *
     * @param userId 用户标识
     * @param safeFilename 安全文件名
     * @return 对象 key
     */
    private static String buildObjectKey(String userId, String safeFilename) {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String safeUserId = sanitizePathSegment(userId);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "user/" + safeUserId + "/" + date + "/" + uuid + "-" + safeFilename;
    }

    /**
     * 将文件名进行简单清洗，避免目录穿越与不可控字符。
     *
     * @param originalFilename 原始文件名
     * @return 安全文件名
     */
    private static String sanitizeFilename(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "file";
        }
        String name = originalFilename.replace('\\', '/');
        int slash = name.lastIndexOf('/');
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        name = name.trim();
        name = name.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (name.length() > 120) {
            name = name.substring(name.length() - 120);
        }
        if (!StringUtils.hasText(name)) {
            return "file";
        }
        return name;
    }

    /**
     * 清洗路径片段（用于 userId 等，避免注入与不可控字符）。
     *
     * @param segment 路径片段
     * @return 安全片段
     */
    private static String sanitizePathSegment(String segment) {
        if (!StringUtils.hasText(segment)) {
            return "anonymous";
        }
        String value = segment.trim();
        value = value.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (value.length() > 64) {
            value = value.substring(0, 64);
        }
        if (!StringUtils.hasText(value)) {
            return "anonymous";
        }
        return value;
    }

    /**
     * 尝试将字符串解析为 Long，失败则返回 null。
     *
     * @param value 字符串
     * @return Long 或 null
     */
    private static Long parseLongOrNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            long v = Long.parseLong(value.trim());
            return v > 0 ? v : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
