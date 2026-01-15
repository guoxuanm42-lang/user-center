# user-center（用户中心）

一个“前后端分离”的用户中心小项目：后端提供注册 / 登录 / Session 登录态 / 管理员接口；前端提供登录、个人资料、后台用户管理等页面。

## 明确说明【这是学习/练手项目】

> 本项目为个人学习 / 练手项目。  
> 用于熟悉 Spring Boot + Vue 的完整开发流程。  
> 并非生产级用户系统。


## 功能清单 / Feature List

### 用户侧

- 用户注册 / 登录（Session 登录态）
- 获取当前登录用户信息
- 修改个人资料
- 退出登录
- 查看 Session 剩余有效时间

### 管理员侧

- 用户列表查询（支持 `username` 模糊搜索）
- 用户信息修改 / 逻辑删除
- 角色管理（角色列表 / 新建角色）
- 用户角色分配（当前实现：一个用户最多一个角色）

### 其他

- 参数校验（`@Valid`）
- 统一返回体（`ApiResponse` + `GlobalResponseAdvice`）
- 统一异常处理（`GlobalExceptionHandler`）
- 管理员鉴权拦截（拦截 `/admin/**`）
- Swagger / OpenAPI 接口文档

## 整体架构说明

本项目采用典型的前后端分离架构：前端负责页面展示与交互，后端负责业务与数据，二者通过 HTTP 接口通信。

- 前端（`user-center-web`）
- 负责页面展示与用户交互（登录、个人资料、后台管理页面等）
- 通过 Axios 调用后端接口（比如 `/user/login`、`/admin/user/search`）
- 开发环境通过 Vite 代理把 `/user`、`/admin` 转发到后端，避免跨域

- 后端（`user-center`）
- 提供 RESTful API（`/user/**`、`/admin/**`）
- 使用 Session 维护登录态（登录成功后写入 `USER_LOGIN_STATE`，浏览器用 `JSESSIONID` 识别会话）
- 通过拦截器统一做管理员鉴权（拦截 `/admin/**`，未登录 401、无权限 403）
- 使用 MyBatis-Plus 操作数据库，`isDelete` 字段实现逻辑删除
- 统一响应与异常：正常返回用 `ApiResponse` 包装，异常由全局异常处理器统一输出

- 数据库（MySQL）
- 三张核心表：`user`（用户）、`role`（角色字典）、`user_role`（用户-角色关系）
- 当前设计：`user_role.user_id` 唯一，因此一个用户最多绑定一个角色

- 通信方式
- 前端 <-> 后端：HTTP（JSON）
- 后端 <-> 数据库：MyBatis-Plus（CRUD）

## 技术栈

- 后端：Spring Boot 3.5.8、MyBatis-Plus 3.5.5、MySQL、Springdoc OpenAPI（Swagger UI）
- 前端：Vue 3、Vite、Pinia、Vue Router、Axios

## 主要依赖



### 后端（Maven / pom.xml）

- `spring-boot-starter-web`：提供 HTTP 接口（Controller / Tomcat）
- `spring-boot-starter-validation`：参数校验（`@Valid` 等）
- `mybatis-plus-spring-boot3-starter`：数据库 CRUD（少写很多 SQL）
- `mysql-connector-j`：连接 MySQL 的驱动
- `lombok`：自动生成 getter/setter（少写样板代码）
- `springdoc-openapi-starter-webmvc-ui`：Swagger UI + OpenAPI 文档
- `spring-boot-devtools`（可选）：开发热重载

Lombok 注意点：

- 如果你在 IDE 里看到 `@Data` 等注解报红，通常是 IDE 没装 Lombok 支持
- IntelliJ IDEA：安装 Lombok 插件，并开启 Annotation Processing

### 前端（npm / package.json）

- 运行依赖：Vue 3、Vue Router、Pinia、Axios
- 开发依赖：Vite、TypeScript、vue-tsc、ESLint、Prettier

## 目录结构

- `user-center/`：后端（Spring Boot）
- `user-center-web/`：前端（Vue + Vite）

## 快速开始（Windows）

### 1）准备环境

- JDK 21
- MySQL（建议 8.x）
- Node.js（建议 18+）

### 2）初始化数据库

1. 在 MySQL 里创建数据库（名字和后端配置一致，默认是 `yupi`）：

   ```sql
   CREATE DATABASE IF NOT EXISTS yupi DEFAULT CHARACTER SET utf8mb4;
   ```

2. 创建 `user` 表（下面这份结构对齐你现在库里的 `user` 表字段）：

   ```sql
   USE yupi;

   CREATE TABLE IF NOT EXISTS `user` (
     `id` BIGINT NOT NULL AUTO_INCREMENT,
     `username` VARCHAR(256) DEFAULT NULL,
     `userAccount` VARCHAR(256) NOT NULL,
     `avatarUrl` VARCHAR(1024) DEFAULT NULL,
     `gender` TINYINT DEFAULT NULL,
     `userPassword` VARCHAR(512) NOT NULL,
     `phone` VARCHAR(128) DEFAULT NULL,
     `email` VARCHAR(512) DEFAULT NULL,
     `userStatus` INT NOT NULL DEFAULT 0,
     `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `updateTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     `isDelete` TINYINT(1) NOT NULL DEFAULT 0,
     `userRole` TINYINT(1) NOT NULL DEFAULT 0,
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_user_account` (`userAccount`)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
   ```

3. 导入角色与用户-角色关联表：

   - SQL 文件位置：`user-center/src/main/resources/schema_role.sql`
   - 这份脚本会创建 `role`、`user_role`，并插入一些初始角色（包含 `ADMIN`）

### 3）启动后端（Spring Boot）

1. 进入后端目录：

   ```powershell
   cd .\user-center\
   ```

2. 配置数据库连接（推荐用环境变量）：

   当前后端读取的环境变量是：

   - 必填：`DB_PASSWORD`（数据库密码）
   - 可选：`DB_URL`（数据库连接串，不配则使用本地默认值）
   - 可选：`DB_USERNAME`（数据库账号，不配则默认 `root`）

   临时生效（只对当前 PowerShell 窗口有效，关闭窗口就没了）：

   ```powershell
   $env:DB_PASSWORD="你的真实数据库密码"
   # 可选：
   # $env:DB_USERNAME="root"
   # $env:DB_URL="jdbc:mysql://localhost:3306/yupi?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&allowPublicKeyRetrieval=true"
   ```

   永久生效（写入系统环境变量，新开 PowerShell 才会生效）：

   ```powershell
   setx DB_PASSWORD "你的真实数据库密码"
   ```

3. 启动：

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. 验证是否启动成功：

   - 后端默认端口：`http://localhost:8090`
   - Swagger UI：`http://localhost:8090/swagger-ui/index.html`
   - OpenAPI JSON：`http://localhost:8090/v3/api-docs`

### 4）启动前端（Vue + Vite）

1. 进入前端目录：

   ```powershell
   cd ..\user-center-web\
   ```

2. 安装依赖并启动：

   ```powershell
   npm install
   npm run dev
   ```

3. 打开页面：

   - 前端默认端口：`http://localhost:3000`
   - 已配置代理：`/user`、`/admin` 会自动转发到后端 `http://localhost:8090`

## 数据库表设计（三张表）

数据库里这三张表”：`user`、`role`、`user_role`。

### 1）user（用户表）

存“用户本体信息”，比如账号、密码、头像、状态等。

- `id`：主键，自增
- `username`：昵称/用户名
- `userAccount`：登录账号（唯一）
- `userPassword`：登录密码（项目里是加盐 MD5 后存库）
- `avatarUrl`：头像链接
- `gender`：性别
- `phone` / `email`：联系方式
- `userStatus`：用户状态（例如 0 正常、1 封禁）
- `userRole`：老的“单字段角色”（0 普通用户、1 管理员）
- `createTime` / `updateTime`：创建时间 / 更新时间
- `isDelete`：逻辑删除标记（0 未删、1 已删）

### 2）role（角色表）

这是“角色字典”，只负责记录系统里有哪些角色（ADMIN、DRIVER 等）。

- `id`：主键，自增
- `role_key`：角色标识（唯一，比如 `ADMIN`）
- `role_name`：角色中文名（比如 管理员）
- `description`：描述
- `status`：状态（0 启用、1 禁用）
- `createTime` / `updateTime`：创建时间 / 更新时间
- `isDelete`：逻辑删除标记（0 未删、1 已删）

### 3）user_role（用户-角色关系表）

这张表是“绑定关系”，用来说明“哪个用户拥有哪些角色”。

- `id`：主键，自增
- `user_id`：用户 id（外键关联 `user.id`）
- `role_id`：角色 id（外键关联 `role.id`）
- `createTime`：创建时间

注意：当前 `schema_role.sql` 里对 `user_role.user_id` 加了唯一索引（`UNIQUE KEY uk_user_role_user_id (user_id)`），这代表“一个用户最多绑定一个角色”。这也和后端接口 `/admin/user/roles/assign` 的校验逻辑一致：一次最多只能给用户分配 1 个角色。

## 接口速览

### 普通用户接口

- `POST /user/register`：注册
- `POST /user/login`：登录（成功后用 Session 记录登录态）
- `GET /user/current`：获取当前登录用户（未登录会 401）
- `POST /user/logout`：退出登录
- `POST /user/update`：更新自己的资料（必须登录）
- `GET /user/session/ttl`：查看 Session 过期时间（秒）

### 管理员接口（统一走 `/admin/**`）

- `GET /admin/user/search`：按 username 模糊搜索用户
- `POST /admin/user/delete`：按 id 删除用户（逻辑删除）
- `POST /admin/user/update`：更新用户信息
- `GET /admin/user/roles`：查询用户拥有的角色 id 列表
- `POST /admin/user/roles/assign`：给用户分配角色（当前实现：一个用户最多一个角色）
- `GET /admin/role/list`：查询角色列表
- `POST /admin/role/create`：创建角色

你也可以直接用后端自带的请求示例：

- `user-center/src/main/resources/test.http`

## 权限与登录态
- 本项目使用 Session 做登录态：你登录成功后，后端会在响应里给浏览器一个 `JSESSIONID`（Cookie）。
- 之后你访问需要登录的接口，浏览器会自动带上这个 Cookie，后端就知道“你是谁”。
- 管理员接口统一拦截 `/admin/**`：
  - 没登录：返回 401（`NOT_LOGIN`）
  - 不是管理员：返回 403（`NO_AUTH`）

管理员判断规则（满足任一即可）：

1. `user.userRole == 1`（user 表里的管理员标记）
2. 或者：在 `user_role` 里把这个用户绑定到 `role_key = 'ADMIN'` 的角色（通过 user_role -> role 这条关系判断）

如果你想把某个用户变成管理员，有两种常见做法（两选一即可）：

1）直接改 user 表的标记：

```sql
UPDATE `user` SET `userRole` = 1 WHERE id = 你的用户id;
```

2）通过角色表绑定（更“正规”，扩展性更好）：

```sql
-- 先确认 ADMIN 角色的 id
SELECT id FROM role WHERE role_key = 'ADMIN' AND isDelete = 0 AND status = 0;

-- 再把用户绑定到 ADMIN（注意：user_role 的 user_id 是唯一的，所以同一用户只能保留一条绑定）
INSERT INTO user_role(user_id, role_id) VALUES (你的用户id, ADMIN角色id)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);
```

## 常见问题

- 调用管理员接口一直 401：先调用 `/user/login` 登录，并确保请求能带上 Cookie（例如 Postman/浏览器会自动带，手写 curl 需要自己带 Cookie）。
- 前端请求报跨域：正常情况下不会，因为 `user-center-web/vite.config.ts` 已把 `/user`、`/admin` 代理到后端 `8090`。
- 导入 `schema_role.sql` 报错：请先创建 `user` 表（因为 `user_role` 有外键引用 `user(id)`）。
## 项目演示截图
<img width="1635" height="734" alt="image" src="https://github.com/user-attachments/assets/6e00cb8d-6519-4385-a6de-73813c1e06ed" />
<img width="1909" height="761" alt="image" src="https://github.com/user-attachments/assets/81c48dde-90ba-4e9d-bb42-e6cd572860ef" />
<img width="1915" height="728" alt="image" src="https://github.com/user-attachments/assets/3881f577-d52b-4f91-9ee4-5965ee23f1e2" />
<img width="1873" height="795" alt="image" src="https://github.com/user-attachments/assets/d3619f4b-f895-42d2-aaa5-bee9fff3dcf3" />
<img width="1649" height="788" alt="image" src="https://github.com/user-attachments/assets/d055a92e-4047-4ffb-9442-2d6268047906" />
<img width="1846" height="746" alt="image" src="https://github.com/user-attachments/assets/eec3e402-31c5-4d1f-981e-5f3c62410a60" />

## 后续可扩展方向（Spring Security + JWT + Redis）

### 1）引入 Spring Security（统一认证与授权）

当前实现：
- 使用自定义拦截器判断登录态
- 管理员权限通过代码手动校验
- 登录态：Http Session（`USER_LOGIN_STATE` + `JSESSIONID`）
- 管理员鉴权：自定义拦截器拦截 `/admin/**`，在拦截器里判断是否管理员
- 授权粒度：偏“路径级别”（只要是 `/admin/**` 就需要管理员）

可扩展为：
- 使用 Spring Security 统一接管认证与授权流程
- 自定义 `UserDetailsService`，从数据库加载用户与角色信息
- 使用 `SecurityContext` 保存当前登录用户
- 通过注解方式控制权限，例如：
  - `@PreAuthorize(\"hasRole('ADMIN')\")`
  - `@PreAuthorize(\"hasAuthority('user:delete')\")`

预期收益：
- 认证与授权逻辑标准化、可维护性更高
- 更容易把“路径级权限”升级为“接口级/方法级权限”
- 统一处理 401/403、权限不足提示更一致

### 2）引入 Redis（缓存与登录态增强）

当前实现：
- 登录态基于 Http Session（内存 / 容器级别）
- 服务重启后 Session 会丢失
- 角色判断需要查数据库（例如 user_role + role）

可扩展为：
- 使用 Redis 做 Session 存储（Spring Session + Redis）
- 或将用户登录信息缓存到 Redis（例如 `userId -> 用户信息`）
- 使用 Redis 缓存：
  - 角色 / 权限列表
  - 热点用户信息
  - 管理员鉴权相关的查询结果（减少频繁查库）

预期收益：
- 支持服务重启不丢登录态（取决于你选 Session 还是 Token 方案）
- 提升性能（减少数据库压力）
- 为后续多实例/集群部署打基础

### 3）从 Cookie + Session 升级为 Token（JWT）认证

当前实现：
- 浏览器通过 Cookie 自动携带 `JSESSIONID`
- 后端依赖 Session 识别用户身份

可扩展为：
- 登录成功后返回 JWT（Access Token），前端保存并在请求头携带：
  - `Authorization: Bearer <token>`
- 后端每次请求解析 JWT，拿到用户 id/角色等信息用于鉴权
- 结合 Redis 做“登录态增强”（更接近生产级）：
  - 存 refresh token 或者 token 白名单/黑名单（用于退出登录、强制下线）
  - 记录 token 版本号（用户改密码后让旧 token 失效）

预期收益：
- 后端更接近“无状态”（stateless），更适合水平扩展
- 前后端完全通过 Token 交互，跨域/多端（Web/小程序/App）更方便
- 配合 Redis 可以实现更强的安全能力（登出立即失效、风控等）
- 为微服务架构提供基础能力

