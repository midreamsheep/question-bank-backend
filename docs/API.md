# Vegetable Forum Backend API（完整）

本文档基于当前代码实现整理（而非仅 PRD 设想），用于前后端/测试联调。

- 文档来源：`src/main/java/**/**Controller.java` + `src/main/java/**/presentation/dto/*`
- 最后整理日期：2026-01-29

## 1. 通用约定

### 1.1 Base URL

- Base Path：`/api/v1`

### 1.2 认证与请求头

- JWT：`Authorization: Bearer <token>`
- traceId：客户端可传 `X-Request-Id`，服务端会透传或生成，并回写响应头 `X-Request-Id`

### 1.3 时间格式

- `LocalDate`：`YYYY-MM-DD`
- `LocalDateTime`：ISO-8601（例如：`2026-01-29T12:34:56`）

### 1.4 统一响应体 ApiResponse

成功示例：

```json
{
  "code": 0,
  "message": "OK",
  "data": {}
}
```

失败示例：

```json
{
  "code": 40000,
  "message": "参数错误",
  "data": null
}
```

### 1.5 常用错误码（约定）

| code | 含义 | HTTP |
| --- | --- | --- |
| 40000 | 参数错误 | 400 |
| 40100 | 未认证 | 401 |
| 40300 | 无权限 | 403 |
| 40400 | 资源不存在 | 404 |
| 50000 | 服务端错误 | 500 |
| 50300 | 服务不可用（依赖未启用/不可用） | 503 |

### 1.6 分页约定 PageResponse

列表接口使用页码分页（从 1 开始）：

```json
{
  "items": [],
  "page": 1,
  "pageSize": 20,
  "total": 0
}
```

> 说明：实际返回会被 `ApiResponse` 包裹。

### 1.7 枚举值（常用）

- subject：字符串（不再是枚举；可自定义，例如 `MATH` / `PHYSICS` / `CHEMISTRY` / `化学`）
- ContentFormat：`MARKDOWN` / `LATEX`
- Visibility：`PUBLIC` / `UNLISTED` / `PRIVATE`
- ProblemStatus：`DRAFT` / `PUBLISHED` / `DISABLED`
- CollectionStatus：`ACTIVE` / `DISABLED`
- UserStatus：`ACTIVE` / `DISABLED`
- ReportTargetType：`PROBLEM` / `COMMENT`
- ReportStatus：`OPEN` / `RESOLVED` / `REJECTED`

---

## 2. 系统（System）

### 2.1 健康检查

- `GET /api/v1/health`
- 认证：否
- 响应：`ApiResponse<Void>`

---

## 3. 鉴权（Auth）

### 3.1 登录

- `POST /api/v1/auth/login`
- 认证：否

请求：

```json
{
  "username": "demo",
  "password": "demo"
}
```

响应：

```json
{
  "code": 0,
  "message": "OK",
  "data": { "token": "xxx.yyy.zzz" }
}
```

### 3.2 注册

- `POST /api/v1/auth/register`
- 认证：否

请求：

```json
{
  "username": "demo",
  "password": "demo123",
  "nickname": "Demo"
}
```

响应：

```json
{
  "code": 0,
  "message": "OK",
  "data": { "userId": 1, "token": "xxx.yyy.zzz" }
}
```

### 3.3 退出登录

- `POST /api/v1/auth/logout`
- 认证：是
- 说明：服务端无状态；前端丢弃 token 即可
- 响应：`ApiResponse<Void>`

---

## 4. 用户（User）

### 4.1 获取当前用户资料

- `GET /api/v1/users/me`
- 认证：是

响应（示例字段）：

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "id": 1,
    "username": "demo",
    "nickname": "Demo",
    "avatarFileId": 123,
    "status": "ACTIVE"
  }
}
```

### 4.2 更新当前用户资料

- `PUT /api/v1/users/me`
- 认证：是

请求：

```json
{
  "nickname": "Demo",
  "avatarFileId": 123
}
```

### 4.3 修改当前用户密码

- `PUT /api/v1/users/me/password`
- 认证：是

请求：

```json
{
  "oldPassword": "demo123",
  "newPassword": "demo456"
}
```

### 4.4 我的题目（分页）

- `GET /api/v1/users/me/problems`
- 认证：是

Query：

- `status`（可选）：`DRAFT/PUBLISHED/DISABLED`
- `page`、`pageSize`

响应：`ApiResponse<PageResponse<ProblemSummaryResponse>>`

### 4.5 我的题单（分页）

- `GET /api/v1/users/me/collections`
- 认证：是

Query：

- `status`（可选）：`ACTIVE/DISABLED`
- `page`、`pageSize`

响应：`ApiResponse<PageResponse<CollectionSummaryResponse>>`

### 4.6 我收藏的题目/题单（分页）

- `GET /api/v1/users/me/favorites/problems`
- `GET /api/v1/users/me/favorites/collections`
- 认证：是

Query：`page`、`pageSize`

响应：

- `GET /users/me/favorites/problems`：`ApiResponse<PageResponse<ProblemSummaryResponse>>`
- `GET /users/me/favorites/collections`：`ApiResponse<PageResponse<CollectionSummaryResponse>>`

### 4.7 我点赞的题目/评论（分页）

- `GET /api/v1/users/me/likes/problems`
- `GET /api/v1/users/me/likes/comments`
- 认证：是

Query：`page`、`pageSize`

响应：

- `GET /users/me/likes/problems`：`ApiResponse<PageResponse<ProblemSummaryResponse>>`
- `GET /users/me/likes/comments`：`ApiResponse<PageResponse<ProblemCommentResponse>>`

---

## 5. 管理端：用户与角色（Admin）

### 5.1 用户列表

- `GET /api/v1/admin/users`
- 认证：是（管理员）

Query：

- `keyword`（可选）
- `status`（可选）：`ACTIVE/DISABLED`
- `page`、`pageSize`

响应：`ApiResponse<PageResponse<UserSummaryResponse>>`

### 5.2 用户详情

- `GET /api/v1/admin/users/{id}`
- 认证：是（管理员）

响应：`ApiResponse<UserAdminDetailResponse>`

### 5.3 更新用户状态

- `PUT /api/v1/admin/users/{id}/status`
- 认证：是（管理员）

请求：

```json
{ "status": "DISABLED" }
```

响应：`ApiResponse<UserProfileResponse>`

### 5.4 重置用户密码

- `PUT /api/v1/admin/users/{id}/reset-password`
- 认证：是（管理员）

请求：

```json
{ "newPassword": "new-password" }
```

响应：`ApiResponse<Void>`

### 5.5 用户角色

- `GET /api/v1/admin/users/{id}/roles`
- `PUT /api/v1/admin/users/{id}/roles`
- 认证：是（管理员）

更新请求：

```json
{ "roleIds": [1, 2] }
```

响应：

- `GET /admin/users/{id}/roles`：`ApiResponse<List<RoleResponse>>`
- `PUT /admin/users/{id}/roles`：`ApiResponse<Void>`

### 5.6 删除用户（软删除）

- `DELETE /api/v1/admin/users/{id}`
- 认证：是（管理员）

响应：`ApiResponse<Void>`

### 5.7 角色管理

- `GET /api/v1/admin/roles`（列表）
- `POST /api/v1/admin/roles`（创建）
- `PUT /api/v1/admin/roles/{id}`（更新）
- `DELETE /api/v1/admin/roles/{id}`（删除）
- 认证：是（管理员）

创建请求：

```json
{ "code": "ADMIN", "name": "管理员" }
```

响应：

- `GET /admin/roles`：`ApiResponse<List<RoleResponse>>`
- `POST /admin/roles`：`ApiResponse<RoleResponse>`
- `PUT /admin/roles/{id}`：`ApiResponse<RoleResponse>`
- `DELETE /admin/roles/{id}`：`ApiResponse<Void>`

---

## 6. 分类/题型/标签（Taxonomy）

### 6.1 分类

- `GET /api/v1/categories`（公开列表）
- `POST /api/v1/admin/categories`（管理端创建）
- `PUT /api/v1/admin/categories/{id}`（管理端更新）

Query（list）：

- `subject`（可选）：字符串（可自定义）

创建/更新请求（字段一致）：

```json
{
  "subject": "MATH",
  "parentId": 1,
  "name": "代数",
  "description": "可选",
  "sortOrder": 0,
  "enabled": true
}
```

响应：

- `GET /categories`：`ApiResponse<List<CategoryResponse>>`
- `POST /admin/categories`：`ApiResponse<CategoryResponse>`
- `PUT /admin/categories/{id}`：`ApiResponse<CategoryResponse>`

### 6.2 题型

- `GET /api/v1/problem-types`
- `POST /api/v1/admin/problem-types`
- `PUT /api/v1/admin/problem-types/{id}`

Query（list）：

- `subject`（可选）：字符串（可自定义）

创建/更新请求（字段一致）：

```json
{
  "subject": "MATH",
  "name": "构造",
  "description": "可选",
  "sortOrder": 0,
  "enabled": true
}
```

响应：

- `GET /problem-types`：`ApiResponse<List<ProblemTypeResponse>>`
- `POST /admin/problem-types`：`ApiResponse<ProblemTypeResponse>`
- `PUT /admin/problem-types/{id}`：`ApiResponse<ProblemTypeResponse>`

### 6.3 标签

- `GET /api/v1/tags`
- `POST /api/v1/admin/tags`

Query（list）：

- `subject`（可选）：字符串（可自定义）
- `keyword`（可选）

创建请求：

```json
{
  "subject": "MATH",
  "name": "柯西不等式"
}
```

响应：

- `GET /tags`：`ApiResponse<List<TagResponse>>`
- `POST /admin/tags`：`ApiResponse<TagResponse>`

---

## 7. 题目（Problem）

### 7.1 创建题目（草稿）

- `POST /api/v1/problems`
- 认证：是

请求（示例）：

```json
{
  "title": "一道不等式题",
  "subject": "MATH",
  "difficulty": 3,
  "statementFormat": "MARKDOWN",
  "statement": "题干 Markdown ...",
  "solutionFormat": "LATEX",
  "solution": "\\\\text{解：...}",
  "visibility": "PUBLIC",
  "tagIds": [3]
}
```

响应：`ApiResponse<ProblemCreateResponse>`

### 7.2 题目列表（公开）

- `GET /api/v1/problems`
- 认证：否（仅返回 `PUBLIC + PUBLISHED`）

Query：

- `subject`（可选）
- `tagIds`（可选，多选，OR 语义；推荐）
- `difficultyMin` / `difficultyMax`（可选）
- `keyword`（可选，标题）
- `sort`（可选）：`LATEST/PUBLISHED_AT/DIFFICULTY/HOT`
- `page`、`pageSize`

响应：`ApiResponse<PageResponse<ProblemSummaryResponse>>`

### 7.3 题目详情

- `GET /api/v1/problems/{id}`
- 认证：按可见性决定

访问规则（当前实现）：

- `PUBLIC + PUBLISHED`：任何人可访问
- 作者本人：可访问自己的非公开/未发布题目

响应：`ApiResponse<ProblemDetailResponse>`

### 7.4 仅链接访问（UNLISTED）

- `GET /api/v1/problems/share/{shareKey}`
- 认证：否
响应：`ApiResponse<ProblemDetailResponse>`

### 7.5 更新题目

- `PUT /api/v1/problems/{id}`
- 认证：是（仅作者）

请求：同创建结构
响应：`ApiResponse<ProblemStatusResponse>`

### 7.6 发布题目

- `POST /api/v1/problems/{id}/publish`
- 认证：是（仅作者）
响应：`ApiResponse<ProblemStatusResponse>`

发布请求（可选）：

```json
{
  "subject": "MATH",
  "tagIds": [1, 3],
  "newTags": ["AM-GM", "Bounds"]
}
```

说明：

- `subject` 可选：不传则沿用草稿中的 `subject`
- `tagIds` 可选：不传则沿用草稿中的 `tagIds`
- `newTags` 可选：发布时创建新标签并加入最终标签列表

### 7.8 删除草稿（软删除）

- `DELETE /api/v1/problems/{id}`
- 认证：是（仅作者，且仅 DRAFT）
响应：`ApiResponse<Void>`

### 7.9 作者下架题目

- `POST /api/v1/problems/{id}/disable`
- 认证：是（仅作者）
响应：`ApiResponse<ProblemStatusResponse>`

### 7.10 收藏题目

- `POST /api/v1/problems/{id}/favorite`
- `DELETE /api/v1/problems/{id}/favorite`
- 认证：是
响应：`ApiResponse<Void>`

### 7.11 点赞题目

- `POST /api/v1/problems/{id}/like`
- `DELETE /api/v1/problems/{id}/like`
- 认证：是
响应：`ApiResponse<Void>`

### 7.12 管理端：下架

- `POST /api/v1/admin/problems/{id}/disable`（下架）
- 认证：是（管理员）

响应：`ApiResponse<ProblemStatusResponse>`

---

## 8. 评论（Problem Comment）

### 8.1 评论列表（分页，支持楼中楼）

- `GET /api/v1/problems/{problemId}/comments`
- 认证：按题目可见性决定；公开题目可匿名访问

Query：

- `parentId`（可选；不传表示顶层评论；传 parentId 表示获取该评论的回复列表）
- `page`、`pageSize`

响应：`ApiResponse<PageResponse<ProblemCommentResponse>>`

响应字段要点：

- `deleted=true` 时：`content=null`（软删除占位）
- `likeCount`：评论点赞数

### 8.2 创建评论（支持楼中楼/回复）

- `POST /api/v1/problems/{problemId}/comments`
- 认证：是

请求：

```json
{
  "parentId": 10,
  "replyToCommentId": 12,
  "content": "同学这个思路很巧"
}
```

说明：

- 顶层评论：`parentId=null`
- 回复：`parentId` 必填；`replyToCommentId` 可选（为空默认等于 parentId）

响应：`ApiResponse<Long>`（返回评论ID）

### 8.3 删除评论

- `DELETE /api/v1/problems/{problemId}/comments/{commentId}`
- 认证：是（评论作者或管理员）
响应：`ApiResponse<Void>`

### 8.4 点赞评论

- `POST /api/v1/problems/{problemId}/comments/{commentId}/like`
- `DELETE /api/v1/problems/{problemId}/comments/{commentId}/like`
- 认证：是
响应：`ApiResponse<Void>`

---

## 9. 题单（Collection）

### 9.1 创建题单

- `POST /api/v1/collections`
- 认证：是

请求：

```json
{
  "name": "不等式训练",
  "description": "按题型整理",
  "visibility": "PUBLIC"
}
```

响应：`ApiResponse<CollectionCreateResponse>`

### 9.2 题单列表（公开）

- `GET /api/v1/collections`
- 认证：否（仅返回 `PUBLIC + ACTIVE`）

Query：`page`、`pageSize`
响应：`ApiResponse<PageResponse<CollectionSummaryResponse>>`

### 9.3 题单详情

- `GET /api/v1/collections/{id}`
- 认证：按可见性决定
响应：`ApiResponse<CollectionDetailResponse>`

### 9.4 仅链接访问（UNLISTED）

- `GET /api/v1/collections/share/{shareKey}`
- 认证：否
响应：`ApiResponse<CollectionDetailResponse>`

### 9.5 更新题单（仅作者）

- `PUT /api/v1/collections/{id}`
- 认证：是

请求：

```json
{
  "name": "不等式训练（更新）",
  "description": "简介...",
  "visibility": "UNLISTED"
}
```

响应：`ApiResponse<CollectionDetailResponse>`

### 9.6 删除题单（软删除，仅作者）

- `DELETE /api/v1/collections/{id}`
- 认证：是
响应：`ApiResponse<Void>`

### 9.7 题单条目

- `POST /api/v1/collections/{id}/items`（添加条目，仅作者）
- `PUT /api/v1/collections/{id}/items/reorder`（调整顺序，仅作者）
- `DELETE /api/v1/collections/{id}/items/{problemId}`（移除条目，仅作者）
响应：`ApiResponse<Void>`

添加请求：

```json
{ "problemId": 1001, "sortOrder": 0 }
```

reorder 请求：

```json
{
  "items": [
    { "problemId": 1001, "sortOrder": 10 },
    { "problemId": 1002, "sortOrder": 20 }
  ]
}
```

### 9.8 收藏题单

- `POST /api/v1/collections/{id}/favorite`
- `DELETE /api/v1/collections/{id}/favorite`
- 认证：是
响应：`ApiResponse<Void>`

---

## 10. 每日一题（Daily Problem）

### 10.1 今日每日一题

- `GET /api/v1/daily-problem/today`
- 认证：否
响应：`ApiResponse<List<DailyProblemResponse>>`

### 10.2 指定日期每日一题

- `GET /api/v1/daily-problem?day=YYYY-MM-DD`
- 认证：否
响应：`ApiResponse<List<DailyProblemResponse>>`

### 10.3 历史列表（分页）

- `GET /api/v1/daily-problems`
- 认证：否

Query：

- `from`（可选）
- `to`（可选）
- `page`、`pageSize`
响应：`ApiResponse<PageResponse<DailyProblemResponse>>`

### 10.4 发布/撤回

- `POST /api/v1/admin/daily-problems`（发布；同一天可多条）
- `POST /api/v1/admin/daily-problems/{day}/revoke`（撤回当天全部）
- `POST /api/v1/admin/daily-problem-items/{id}/revoke`（按 ID 撤回单条）
- 认证：是
- 权限：
  - 发布：登录用户
  - 撤回：管理员
响应：
- 发布：`ApiResponse<DailyProblemResponse>`
- 撤回当天全部：`ApiResponse<List<DailyProblemResponse>>`
- 按 ID 撤回单条：`ApiResponse<DailyProblemResponse>`

发布请求：

```json
{
  "day": "2026-01-28",
  "problemId": 1001,
  "copywriting": "今日推荐：注意构造"
}
```

---

## 11. 文件（MinIO）

### 11.1 上传文件

- `POST /api/v1/files`
- 认证：是
- Content-Type：`multipart/form-data`

参数：

- `file`：文件

响应（示例）：

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "id": 123,
    "shareKey": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
    "shareUrl": "/api/v1/files/share/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
    "objectKey": "user/demo/20260101/xxx-a.png",
    "originalFilename": "a.png",
    "size": 12345,
    "contentType": "image/png"
  }
}
```
响应：`ApiResponse<FileUploadResponse>`

### 11.2 获取永久链接信息（fileId -> shareUrl）

- `GET /api/v1/files/share-url`
- 认证：是

Query：

- `fileId`（必填）

响应（示例）：

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "shareKey": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
    "shareUrl": "/api/v1/files/share/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
  }
}
```

### 11.3 获取下载预签名 URL

- `GET /api/v1/files/presigned-get-url`
- 认证：是

Query：

- `fileId`（与 objectKey 二选一）
- `objectKey`（与 fileId 二选一）
- `expiresSeconds`（可选，默认 3600，最小 60）

响应（示例）：

```json
{
  "code": 0,
  "message": "OK",
  "data": { "url": "https://..." }
}
```
响应：`ApiResponse<PresignedUrlResponse>`

### 11.4 永久链接访问（推荐给前端 img/src）

- `GET /api/v1/files/share/{shareKey}`
- 认证：否（持有 shareKey 即可访问）
- 说明：返回二进制文件流（非 ApiResponse 包裹），适合 `<img src="...">` 直接引用

---

## 12. 举报（Report）

### 12.1 创建举报

- `POST /api/v1/reports`
- 认证：是

请求：

```json
{
  "targetType": "PROBLEM",
  "targetId": 1001,
  "reason": "疑似侵权/内容不当"
}
```
响应：`ApiResponse<Long>`（返回举报ID）

### 12.2 管理端：举报列表（分页）

- `GET /api/v1/admin/reports`
- 认证：是（管理员）

Query（可选）：

- `targetType=PROBLEM/COMMENT`
- `status=OPEN/RESOLVED/REJECTED`
- `page`、`pageSize`
响应：`ApiResponse<PageResponse<ReportResponse>>`

### 12.3 管理端：更新举报状态

- `POST /api/v1/admin/reports/{id}/status`
- 认证：是（管理员）

请求：

```json
{
  "status": "RESOLVED",
  "note": "已下架内容"
}
```
响应：`ApiResponse<Void>`

---

## 13. Swagger

- Swagger UI：`/swagger-ui.html`
- OpenAPI JSON：`/v3/api-docs`

---

## 14. 数据结构（DTO 定义）

> 说明：所有接口响应均为 `ApiResponse<T>`；分页列表为 `ApiResponse<PageResponse<T>>`。

### 14.1 Auth

#### LoginRequest

```json
{
  "username": "string",
  "password": "string"
}
```

#### LoginResponse

```json
{
  "token": "string"
}
```

#### RegisterRequest

```json
{
  "username": "string",
  "password": "string",
  "nickname": "string"
}
```

#### RegisterResponse

```json
{
  "userId": 1,
  "token": "string"
}
```

### 14.2 User

#### UserProfileResponse

```json
{
  "id": 1,
  "username": "string",
  "nickname": "string",
  "avatarFileId": 123,
  "status": "ACTIVE"
}
```

#### UserProfileUpdateRequest

```json
{
  "nickname": "string",
  "avatarFileId": 123
}
```

#### UserPasswordChangeRequest

```json
{
  "oldPassword": "string",
  "newPassword": "string"
}
```

### 14.3 Admin：用户与角色

#### UserSummaryResponse（管理端用户列表项）

```json
{
  "id": 1,
  "username": "string",
  "nickname": "string",
  "status": "ACTIVE",
  "lastLoginAt": "2026-01-29T12:34:56",
  "createdAt": "2026-01-01T00:00:00"
}
```

#### UserAdminDetailResponse（管理端用户详情）

```json
{
  "id": 1,
  "username": "string",
  "nickname": "string",
  "avatarFileId": 123,
  "status": "ACTIVE",
  "lastLoginAt": "2026-01-29T12:34:56",
  "createdAt": "2026-01-01T00:00:00",
  "roles": [
    { "id": 1, "code": "ADMIN", "name": "管理员" }
  ]
}
```

#### UserStatusUpdateRequest

```json
{ "status": "DISABLED" }
```

#### UserPasswordResetRequest

```json
{ "newPassword": "string" }
```

#### UserRolesUpdateRequest

```json
{ "roleIds": [1, 2] }
```

#### RoleCreateRequest

```json
{ "code": "ADMIN", "name": "管理员" }
```

#### RoleUpdateRequest

```json
{ "name": "管理员（更新）" }
```

#### RoleResponse

```json
{ "id": 1, "code": "ADMIN", "name": "管理员" }
```

### 14.4 Taxonomy

#### CategoryCreateRequest / CategoryUpdateRequest

```json
{
  "subject": "MATH",
  "parentId": 1,
  "name": "代数",
  "description": "可选",
  "sortOrder": 0,
  "enabled": true
}
```

#### CategoryResponse

```json
{
  "id": 1,
  "subject": "MATH",
  "parentId": null,
  "name": "代数",
  "description": "可选",
  "sortOrder": 0,
  "enabled": true
}
```

#### ProblemTypeCreateRequest / ProblemTypeUpdateRequest

```json
{
  "subject": "MATH",
  "name": "构造",
  "description": "可选",
  "sortOrder": 0,
  "enabled": true
}
```

#### ProblemTypeResponse

```json
{
  "id": 1,
  "subject": "MATH",
  "name": "构造",
  "description": "可选",
  "sortOrder": 0,
  "enabled": true
}
```

#### TagCreateRequest

```json
{ "subject": "MATH", "name": "柯西不等式" }
```

#### TagResponse

```json
{ "id": 1, "subject": "MATH", "name": "柯西不等式" }
```

### 14.5 Problem

#### ProblemCreateRequest / ProblemUpdateRequest

```json
{
  "title": "一道不等式题",
  "subject": "MATH",
  "difficulty": 3,
  "statementFormat": "MARKDOWN",
  "statement": "题干 ...",
  "solutionFormat": "LATEX",
  "solution": "\\\\text{解：...}",
  "visibility": "PUBLIC",
  "tagIds": [3]
}
```

#### ProblemPublishRequest（可选）

```json
{
  "subject": "MATH",
  "tagIds": [1, 3],
  "newTags": ["AM-GM", "Bounds"]
}
```

#### ProblemCreateResponse

```json
{
  "id": 1001,
  "status": "DRAFT",
  "visibility": "PUBLIC",
  "shareKey": null
}
```

#### ProblemStatusResponse

```json
{
  "id": 1001,
  "status": "PUBLISHED",
  "visibility": "PUBLIC",
  "shareKey": null
}
```

#### ProblemAuthorResponse

```json
{
  "id": 1,
  "nickname": "demo",
  "displayName": "demo"
}
```

#### ProblemSummaryResponse

```json
{
  "id": 1001,
  "title": "一道不等式题",
  "subject": "MATH",
  "difficulty": 3,
  "status": "PUBLISHED",
  "visibility": "PUBLIC",
  "publishedAt": "2026-01-29T12:34:56",
  "author": { "id": 1, "nickname": "demo", "displayName": "demo" },
  "tagIds": [1, 3],
  "tags": [
    { "id": 1, "name": "AM-GM" },
    { "id": 3, "name": "Bounds" }
  ]
}
```

#### ProblemDetailResponse

```json
{
  "id": 1001,
  "title": "一道不等式题",
  "subject": "MATH",
  "difficulty": 3,
  "statementFormat": "MARKDOWN",
  "statement": "题干 ...",
  "solutionFormat": "LATEX",
  "solution": "\\\\text{解：...}",
  "visibility": "PUBLIC",
  "shareKey": null,
  "status": "PUBLISHED",
  "author": { "id": 1, "nickname": "demo", "displayName": "demo" },
  "tagIds": [1, 3],
  "tags": [
    { "id": 1, "name": "AM-GM" },
    { "id": 3, "name": "Bounds" }
  ]
}
```

### 14.6 Comment

#### ProblemCommentCreateRequest

```json
{
  "parentId": 10,
  "replyToCommentId": 12,
  "content": "同学这个思路很巧"
}
```

#### ProblemCommentResponse

```json
{
  "id": 1,
  "problemId": 1001,
  "userId": 1,
  "parentId": null,
  "replyToCommentId": null,
  "content": "评论内容",
  "likeCount": 0,
  "deleted": false,
  "createdAt": "2026-01-29T12:34:56"
}
```

### 14.7 Collection

#### CollectionCreateRequest / CollectionUpdateRequest

```json
{
  "name": "不等式训练",
  "description": "按题型整理",
  "visibility": "PUBLIC"
}
```

#### CollectionCreateResponse

```json
{
  "id": 2001,
  "status": "ACTIVE",
  "visibility": "PUBLIC",
  "shareKey": null
}
```

#### CollectionItemRequest

```json
{ "problemId": 1001, "sortOrder": 0 }
```

#### CollectionReorderRequest

```json
{
  "items": [
    { "problemId": 1001, "sortOrder": 10 },
    { "problemId": 1002, "sortOrder": 20 }
  ]
}
```

#### CollectionSummaryResponse

```json
{
  "id": 2001,
  "name": "不等式训练",
  "description": "按题型整理",
  "visibility": "PUBLIC",
  "status": "ACTIVE",
  "itemCount": 2,
  "authorId": 1
}
```

#### CollectionDetailResponse

```json
{
  "id": 2001,
  "name": "不等式训练",
  "description": "按题型整理",
  "visibility": "PUBLIC",
  "shareKey": null,
  "status": "ACTIVE",
  "items": [
    { "problemId": 1001, "sortOrder": 10 }
  ]
}
```

### 14.8 Daily Problem

#### DailyProblemPublishRequest

```json
{
  "day": "2026-01-29",
  "problemId": 1001,
  "copywriting": "今日推荐：注意构造"
}
```

#### DailyProblemResponse

```json
{
  "id": 90001,
  "day": "2026-01-29",
  "copywriting": "今日推荐：注意构造",
  "problem": { "id": 1001, "title": "一道不等式题", "subject": "MATH", "difficulty": 3 }
}
```

### 14.9 File

#### FileUploadResponse

```json
{
  "id": 123,
  "shareKey": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
  "shareUrl": "/api/v1/files/share/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
  "objectKey": "user/1/20260129/xxxxxxxxxxxx-a.png",
  "originalFilename": "a.png",
  "size": 12345,
  "contentType": "image/png"
}
```

#### FileShareResponse

```json
{
  "shareKey": "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
  "shareUrl": "/api/v1/files/share/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
}
```

#### PresignedUrlResponse

```json
{ "url": "https://..." }
```

### 14.10 Report

#### ReportCreateRequest

```json
{
  "targetType": "PROBLEM",
  "targetId": 1001,
  "reason": "疑似侵权/内容不当"
}
```

#### ReportResponse

```json
{
  "id": 1,
  "reporterId": 1,
  "targetType": "PROBLEM",
  "targetId": 1001,
  "reason": "疑似侵权/内容不当",
  "status": "OPEN",
  "handlerId": null,
  "handledAt": null,
  "handlingNote": null,
  "createdAt": "2026-01-29T12:34:56"
}
```

#### ReportUpdateStatusRequest

```json
{ "status": "RESOLVED", "note": "已下架内容" }
```
