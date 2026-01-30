-- 测试数据：用于 AuthControllerWebMvcTest 等需要真实凭证校验的用例。
-- 密码 demo 的 BCrypt 哈希（由 BCryptPasswordEncoder 生成）。
MERGE INTO vf_user (
  id,
  username,
  password_hash,
  nickname,
  avatar_file_id,
  status,
  last_login_at,
  created_at,
  updated_at,
  deleted
) KEY (id) VALUES (
  1,
  'demo',
  '$2a$10$HhdNF/oJWYOs9Chmxg2ALeAl/xEN6ktzDn.JcHc7hZ6DZmWu1urEm',
  'Demo',
  NULL,
  'ACTIVE',
  NULL,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP,
  0
);
