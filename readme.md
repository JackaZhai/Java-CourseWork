# 学生选课管理系统

学生选课管理系统基于 Spring Boot + Spring Data JPA 构建，实现学生注册、首次登录强制修改密码、在线选课以及后台管理等完整流程，满足课程设计的功能要求。

## 核心功能

- **学生注册**：填写姓名、年龄、手机号、入学时间并选择专业，系统自动生成“专业编号 + 入学年份 + 序号”格式的唯一学号，初始密码为 `123456`。
- **首次登录强制改密**：学生使用学号与初始密码登录后必须先修改密码（需包含字母、数字和特殊字符且不少于 8 位）方可继续操作。
- **选课管理**：学生可查看已选/未选课程、对应学分总和，并执行选课、退课操作，学分统计实时更新。
- **课程管理**：管理员维护课程的增删改查，若课程已被学生选修则禁止删除并给予提示。
- **专业管理**：管理员维护专业信息，若仍有关联学生则禁止删除。
- **账号管理**：管理员可创建、删除系统账号，密码采用 MD5 加密存储。
- **选课查询**：管理员查看所有学生的选课情况，支持按关键字、专业筛选并统计学分。
- **数据库设计**：`docs/model-diagram.md` 中提供了实体关系模型说明。
- **部署指引**：`docs/deployment.md` 给出在笔记本电脑上无 IDE 环境部署运行的步骤。

## 技术栈

- Java 17
- Spring Boot 3
- Spring Data JPA & Hibernate
- Spring Security (自定义 MD5 密码编码器)
- Thymeleaf + HTML/CSS 前端页面
- H2 数据库（开发/演示环境）

## 快速开始

1. 安装 JDK 17 与 Maven 3.9+。
2. 克隆仓库并进入项目目录：
   ```bash
   git clone <repository-url>
   cd Java-CourseWork
   ```
3. 构建并运行应用（默认使用 H2 内存数据库）：
   ```bash
   mvn spring-boot:run
   ```
4. 访问 [http://localhost:8080](http://localhost:8080)。
5. 管理员默认账号：`admin`，密码：`Admin@123`。
6. 学生注册成功后将跳转到登录页，系统会自动填充新生成的学号并提示初始密码为 `123456`。

> **提示**：如需在演示环境保留数据，可将 `src/main/resources/application.properties` 中的 H2 配置调整为文件存储模式，或改用 MySQL/PostgreSQL 并更新对应的 `spring.datasource.*` 配置。

## 运行与测试

- 打包构建（跳过测试）：
  ```bash
  mvn -DskipTests package
  ```
- 运行集成测试：
  ```bash
  mvn test
  ```

## 项目结构概览

```
src/main/java/com/example/coursework
├── config            # 数据初始化等配置
├── controller        # 学生端与管理员端控制器
├── dto               # 表单数据传输对象
├── model             # JPA 实体定义
├── repository        # Spring Data JPA 仓储接口
├── security          # Spring Security 配置与组件
└── service           # 业务服务层
```

更多细节请参考源代码中文注释与 `docs/` 目录下的补充文档。
