# API 变更记录

本文档记录“对外接口行为”层面的重要变更，便于前后端联调与版本同步。

## 2026-01-29：取消题目审核，作者可直接发布

### 变更摘要

- 题目流程从「草稿 → 提交审核 → 管理员审核 → 发布」调整为「草稿 → 直接发布」。
- 题目状态枚举收敛为：`DRAFT` / `PUBLISHED` / `DISABLED`。

### 受影响接口（变更/删除）

#### 删除：作者提交审核

- 删除 `POST /api/v1/problems/{id}/submit-review`
- 前端替代方案：直接调用 `POST /api/v1/problems/{id}/publish`

#### 删除：管理端审核通过/驳回

- 删除 `POST /api/v1/admin/problems/{id}/review/approve`
- 删除 `POST /api/v1/admin/problems/{id}/review/reject`

#### 保留：发布/下架

- 保留 `POST /api/v1/problems/{id}/publish`（作者直接发布）
- 保留 `POST /api/v1/problems/{id}/disable`（作者下架）
- 保留 `POST /api/v1/admin/problems/{id}/disable`（管理员下架）

### 受影响字段与枚举

- `ProblemStatus` 移除 `PENDING_REVIEW`
- 列表筛选的 `status` 参数不再接受 `PENDING_REVIEW`
  - 例如：`GET /api/v1/users/me/problems?status=...`

### 数据库变更与迁移建议

- 移除审核记录表：`vf_problem_review`（新建库不再创建该表）
- 旧库如果已经存在历史状态值 `PENDING_REVIEW`：

```sql
UPDATE vf_problem SET status = 'DRAFT' WHERE status = 'PENDING_REVIEW';
DROP TABLE IF EXISTS vf_problem_review;
```

### 兼容性说明（服务端行为）

- 为避免历史脏数据导致服务端报错：当前代码读取到 `vf_problem.status='PENDING_REVIEW'` 会按 `DRAFT` 处理；
  但仍建议执行上面的 SQL 迁移，确保数据一致。

---

## 2026-01-29：文件上传增加“文件ID”，支持通过 fileId 获取下载 URL

### 变更摘要

- 上传接口在返回中新增 `id`（`vf_file_object.id`），用于与用户头像（`avatarFileId`）等业务字段绑定。
- 上传接口同时生成 `shareKey/shareUrl`，用于前端获得“永久链接”（`<img src>` 直接可用）。
- 获取预签名 URL 接口支持 `fileId` 参数（与 `objectKey` 二选一），以便前端只持久化 `fileId`。
  - `objectKey` 兼容保留（但不推荐前端持久化）。

### 受影响接口（变更）

#### 变更：上传文件返回字段

- `POST /api/v1/files`
- 响应 `data` 新增字段：`id`、`shareKey`、`shareUrl`

#### 变更：预签名下载 URL 支持 fileId

- `GET /api/v1/files/presigned-get-url`
- Query 支持：
  - `fileId`（推荐）
  - `objectKey`（兼容保留）

#### 新增：永久链接访问

- `GET /api/v1/files/share/{shareKey}`（无需登录，返回二进制流）

#### 新增：通过 fileId 获取永久链接信息

- `GET /api/v1/files/share-url?fileId=...`（需登录，返回 shareKey/shareUrl）

### 数据库变更

- 新增表：`vf_file_object`
  - 关键字段：`id`（雪花ID）、`object_key`（唯一）、`original_filename`、`content_type`、`size`、`uploader_id`

---

## 2026-01-29：每日一题发布权限调整为“登录用户可发布”

### 变更摘要

- 每日一题发布接口不再要求管理员权限；任意登录用户均可发布/替换每日一题。
- 每日一题撤回接口仍要求管理员权限。

### 受影响接口（行为变更）

- `POST /api/v1/admin/daily-problems`
  - 之前：需管理员
  - 现在：需登录（不要求管理员）
- `POST /api/v1/admin/daily-problems/{day}/revoke`
  - 仍需管理员

---

## 2026-01-29：每日一题支持“同一天多道题”

### 变更摘要

- 每日一题不再限制“一天只能一条记录”，同一天可发布多条。
- `GET /api/v1/daily-problem/today` 与 `GET /api/v1/daily-problem?day=...` 返回类型从单条改为列表。
- 新增按 ID 撤回单条每日一题接口；原按日期撤回接口改为撤回当天全部。

### 受影响接口（行为变更/新增）

- `GET /api/v1/daily-problem/today`
  - 之前：`ApiResponse<DailyProblemResponse>`
  - 现在：`ApiResponse<List<DailyProblemResponse>>`
- `GET /api/v1/daily-problem?day=YYYY-MM-DD`
  - 之前：`ApiResponse<DailyProblemResponse>`
  - 现在：`ApiResponse<List<DailyProblemResponse>>`
- `POST /api/v1/admin/daily-problems/{day}/revoke`
  - 之前：撤回当天唯一一条
  - 现在：撤回当天全部
- 新增：`POST /api/v1/admin/daily-problem-items/{id}/revoke`（撤回单条）

### 数据库变更

- `vf_daily_problem` 取消 `day` 唯一约束，改为 `(day, problem_id)` 唯一，允许同一天多条。

---

## 2026-01-29：题目 author 字段补全可展示名称（nickname/displayName）

### 变更摘要

- 题目相关响应中的 `author.nickname` 由服务端填充为稳定可展示值（昵称为空时回退用户名/“用户 {id}”）。
- `author` 新增字段 `displayName`（与 nickname 一致，可作为更明确的“可展示名称”使用）。
- `GET /api/v1/problems/{id}` 的详情响应新增 `author` 字段。

### 受影响接口（行为变更）

- `GET /api/v1/problems/{id}`：响应 `data` 新增 `author`
- `GET /api/v1/problems`、`GET /api/v1/users/me/problems`、`GET /api/v1/users/me/favorites/problems`、`GET /api/v1/users/me/likes/problems`
  - `items[].author.nickname` 从可能为 `null` 调整为稳定可展示值
  - `items[].author.displayName` 新增

---

## 2026-01-29：题目接口收敛为「学科 + 标签（多选）」（不兼容变更）

### 变更摘要

- `GET /api/v1/problems` 查询参数收敛为 `subject` + `tagIds`（多选，OR 语义）。
- 删除旧的单选/分类/题型筛选参数：`tagId`、`categoryId`、`typeId`。
- `POST /api/v1/problems` 与 `PUT /api/v1/problems/{id}` 删除请求字段：`categoryIds`、`typeIds`（仅保留 `tagIds`）。
- `POST /api/v1/problems/{id}/publish` 支持发布时补充 `subject/tagIds`，并通过 `newTags` 创建新标签。
- 题目列表/详情响应增加 `tagIds` 与 `tags`，减少前端额外拉取标签再映射的工作量。

### 受影响接口（行为变更/新增字段）

- `GET /api/v1/problems`
  - Query：`tagIds=1&tagIds=3`（OR 语义）
- `GET /api/v1/problems`、`GET /api/v1/users/me/problems`、`GET /api/v1/users/me/favorites/problems`、`GET /api/v1/users/me/likes/problems`
  - 响应 `items[]` 新增：`tagIds`、`tags`
- `GET /api/v1/problems/{id}` 与 `GET /api/v1/problems/share/{shareKey}`
  - 响应 `data` 新增：`tagIds`、`tags`
