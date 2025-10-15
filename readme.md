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

## 运行步骤

1. 创建数据库并执行 `src/main/resources/schema.sql` 初始化结构与管理员账号。
2. 修改 `src/main/resources/db.properties` 中的数据库连接配置。
3. 使用 Maven 构建项目：

   ```bash
   mvn clean package
   ```

4. 将生成的 `employee-management.war` 部署至支持 Servlet 4.0 的容器（如 Tomcat 9+）。
5. 访问 `http://localhost:8080/employee-management/login`，使用初始化账号登录。

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
