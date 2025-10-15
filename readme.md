# 员工信息管理系统

使用 Maven 构建的 Servlet + JDBC Web 信息系统，实现管理员登录、员工信息管理以及岗位选项管理。

## 功能特性

- 管理员登录，登录成功后才可访问业务页面，登录信息存入账号表。
- 员工管理：
  - 通过表格展示员工列表，支持服务端分页（每页 10 条）。
  - 支持姓名、手机号、性别、岗位、入职时间范围、薪资范围等多条件组合查询。
  - 可新增、编辑、删除员工信息，入职时间使用年月控件，性别使用单选按钮，岗位使用下拉框。
  - 员工编号根据“岗位编号 + 入职年份 + 四位序号”自动生成，不可重复且界面不可编辑。
- 选项管理：维护岗位等基础选项数据，支持分页、条件查询与增删改。

## 技术实现

- Servlet 4.0 + JSP + JSTL 构建 Web 层。
- 采用原生 JDBC 访问数据库，通过 `ResultSetMapper` 工具类 + 注解实现 ORM 映射。
- 通过 `AuthFilter` 实现访问控制。
- 数据库共三张表：`account`、`option_item`、`employee`，主键均由应用生成。
- 使用 `schema.sql` 提供示例建表语句。

## 环境要求

- JDK 8 及以上版本。
- Maven 3.6+。
- 支持 Servlet 4.0 的容器（Tomcat 9+、Jetty 10 等）。
- MySQL（或其他已调整 SQL 的关系型数据库）。

## 运行步骤

1. 创建数据库并执行 `src/main/resources/schema.sql` 初始化结构与管理员账号（默认账号 `admin`，密码 `admin123`）。
2. 修改 `src/main/resources/db.properties` 中的数据库连接配置，确保 JDBC URL、用户名、密码与数据库环境匹配。
3. 使用 Maven 构建项目（若在离线环境需预先下载依赖）：

   ```bash
   mvn clean package
   ```

4. 将生成的 `employee-management.war` 部署至 Servlet 容器，例如将 WAR 放入 Tomcat 的 `webapps/` 目录后启动 Tomcat。
   - 如需自定义上下文路径，可在 `conf/server.xml` 中配置 `<Context path="/employee-management" docBase="/path/to/employee-management.war" />`，或在 `conf/Catalina/localhost/` 下创建相应的 XML。
   - 如果使用 Tomcat 10+，请确保启用了 Jakarta EE 9 兼容模式或改用 Tomcat 9，以避免包名差异导致的 Servlet 加载错误。
5. 启动 Tomcat 后访问 `http://localhost:8080/employee-management/login`，使用初始化账号登录系统。
6. 登录成功后即可访问员工管理、选项管理等页面，进行数据的增删改查操作。

> 💡 **提示**：若需在 IDE（如 IntelliJ IDEA）中直接运行，可配置本地 Tomcat 服务器并指定该 Maven 项目生成的 `war exploded` 目录作为部署工件。

## 目录结构

```
src/main/java
├── annotation       # ORM 注解
├── common           # 通用模型（分页对象等）
├── controller       # Servlet 控制器
├── dao              # JDBC 数据访问层
├── filter           # 认证过滤器
├── model            # 实体类
├── service          # 业务逻辑层
└── util             # 工具类（连接、编号生成等）
```

## 主要说明

- 员工岗位选项请放置在 `option_item` 表中，`category` 建议使用 `JOB`，`value` 字段填写岗位编号前缀。
- `ResultSetMapper` 使用反射与注解将 `ResultSet` 自动转换为实体对象，减少样板代码。
- 系统默认使用 MySQL 数据库，如需其他数据库，请调整 JDBC URL、驱动以及分页 SQL。

## 前端界面

- 本项目使用 JSP + JSTL 构建界面。部署 WAR 后即可通过浏览器访问以下页面：
  - `/login`：管理员登录页。
  - `/employee/list`：员工列表，支持分页与组合查询，可跳转至新增、编辑、查看页面。
  - `/option/list`：选项管理页，用于维护岗位等基础数据。
- 页面静态资源位于 `src/main/webapp/static/` 目录（包含 CSS 与 JavaScript），可根据需求自行调整样式与交互。

## Tomcat 部署补充说明

- **版本选择**：建议使用 Tomcat 9.x。Tomcat 10 默认基于 Jakarta EE 9，Servlet API 包名为 `jakarta.*`，需要额外迁移步骤。
- **JSP 编码设置**：确保 `conf/server.xml` 中 Connector 的 `URIEncoding="UTF-8"`，避免中文参数乱码。
- **数据库驱动**：将 MySQL 驱动（`mysql-connector-java-8.x.jar`）放入 Tomcat 的 `lib/` 目录，或保持项目自带依赖并通过 WAR 部署。
- **日志查看**：登录与业务操作日志可在 `logs/catalina.out` 或对应日期的日志文件中查看，方便排查数据库连接、JDBC 操作等问题。
- **热部署**：开发阶段可在 IDE 中配置 Tomcat，使用 `war exploded` 方式部署，修改 JSP 或静态资源后可快速查看效果；发布环境建议使用正式 WAR 包。
