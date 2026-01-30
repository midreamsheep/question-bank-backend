# 文件存储（MinIO）

本项目使用 MinIO 作为对象存储，用于保存用户上传的文件，并通过“预签名 URL”提供下载能力。

## 1. 配置项

配置前缀：`minio`

- `minio.enabled`：是否启用（默认 `false`）
- `minio.endpoint`：服务地址（例如 `http://localhost:9000`）
- `minio.access-key`：访问 Key
- `minio.secret-key`：访问 Secret
- `minio.bucket`：桶名称（默认 `vegetable-forum`）
- `minio.auto-create-bucket`：是否自动创建桶（默认 `true`）
- `minio.presign-expire-seconds`：预签名 URL 默认有效期（秒，默认 `3600`）

本地示例见：`docs/本地配置示例.yml`

## 2. 本地启动 MinIO（示例）

使用 Docker 启动：

```bash
docker run -p 9000:9000 -p 9001:9001 \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin \
  -v $PWD/.minio-data:/data \
  minio/minio server /data --console-address ":9001"
```

- API 地址：`http://localhost:9000`
- 控制台：`http://localhost:9001`

## 3. 接口说明

注意：除健康检查与登录外，其余接口默认需要携带 JWT。

### 3.1 上传文件

- `POST /api/v1/files`
- Content-Type：`multipart/form-data`
- 参数：`file`

返回：

- `id`：文件ID（后续可用于头像等绑定，推荐前端持久化这个字段）
- `shareUrl`：永久链接（推荐用于 `<img src>`）
- `objectKey`：对象 key（兼容/排查用）

### 3.2 获取下载预签名 URL

- `GET /api/v1/files/presigned-get-url?fileId=...&expiresSeconds=...`
- 或：`GET /api/v1/files/presigned-get-url?objectKey=...&expiresSeconds=...`
- `expiresSeconds` 可选，最小 60 秒

返回：`url`

### 3.3 永久链接访问（推荐给前端 img/src）

- `GET /api/v1/files/share/{shareKey}`
- 无需登录（持有 shareKey 即可访问）
- 返回：二进制文件流（可直接用于 `<img src>`）

### 3.4 通过 fileId 获取 shareUrl（头像等场景推荐）

- `GET /api/v1/files/share-url?fileId=...`
- 认证：是
- 返回：`shareUrl`（前端可拼接为 `http://后端域名{shareUrl}` 用于 `<img src>`）

## 4. 安全与注意事项

- 禁止在日志中输出 JWT 全量 token、访问密钥等敏感信息。
- `objectKey` 由服务端生成并返回，客户端不应自行拼接猜测。
- 生产环境请使用强随机 `minio.secret-key` 并限制网络访问范围。
