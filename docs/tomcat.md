# Tomcat 部署指南

本文档提供将员工信息管理系统部署到 Apache Tomcat 的详细步骤。

## 1. 准备工作

- 安装 JDK 8 或以上版本，并设置好 `JAVA_HOME`。
- 下载并解压 Tomcat 9.x（推荐使用 9.0.8x 最新维护版本）。
- 准备 MySQL 数据库并执行项目中的 `src/main/resources/schema.sql` 初始化表结构与管理员账号。

## 2. 配置数据库连接

1. 编辑项目根目录下的 `src/main/resources/db.properties`，填写数据库地址、用户名、密码。
2. 将 MySQL 驱动包 `mysql-connector-java-8.x.jar` 放入 Tomcat 安装目录的 `lib/` 目录，或确保部署的 WAR 中已经包含驱动依赖。

## 3. 构建 WAR 包

在项目根目录执行：

```bash
mvn clean package
```

完成后会在 `target/` 目录生成 `employee-management.war`。

## 4. 部署到 Tomcat

### 方式一：复制 WAR

1. 将 `employee-management.war` 复制到 Tomcat 的 `webapps/` 目录。
2. 启动 Tomcat：

   ```bash
   bin/startup.sh   # macOS/Linux
   bin/startup.bat  # Windows
   ```
3. Tomcat 启动后会自动解压 WAR 并以 `employee-management` 作为上下文路径。

### 方式二：配置独立 Context

1. 在 Tomcat 的 `conf/Catalina/localhost/` 目录新建 `employee-management.xml`：

   ```xml
   <Context path="/employee-management" docBase="/absolute/path/to/employee-management.war" />
   ```
2. 启动 Tomcat 后即可通过该上下文路径访问。

> ⚠️ 如果使用 Tomcat 10 或更高版本，请确保启用 [Tomcat Migration Tool](https://tomcat.apache.org/download-migration.cgi) 将 Servlet `javax.*` 包名自动转换为 `jakarta.*`，否则建议直接使用 Tomcat 9。

## 5. 访问应用

- 登录地址：`http://localhost:8080/employee-management/login`
- 默认管理员账号：`admin`
- 默认密码：`admin123`

首次登录后系统会记录最后一次登录时间，可在数据库 `account` 表中查看。

## 6. 常见问题

| 问题 | 解决方案 |
| ---- | -------- |
| 页面中文乱码 | 编辑 `conf/server.xml`，在 HTTP Connector 中添加 `URIEncoding="UTF-8"`。 |
| 数据库连接失败 | 检查数据库是否开启远程访问、账号密码是否正确，及防火墙是否放行端口。 |
| Maven 构建失败 | 在离线环境提前下载依赖，或在有网络环境执行一次 `mvn dependency:go-offline`。 |
| 访问 404 | 确认上下文路径与访问 URL 是否一致，Tomcat 是否成功解压 WAR。 |

## 7. 停止与重启

- 停止：`bin/shutdown.sh` 或 `bin/shutdown.bat`
- 重启：先停止再启动，或使用 `bin/catalina.sh stop` / `start` 组合命令。

## 8. 日志位置

- `logs/catalina.out`：Tomcat 标准输出与错误日志。
- `logs/localhost.YYYY-MM-DD.log`：当天应用访问日志，便于排查 Servlet 抛出的异常。

完成以上步骤后，即可在 Tomcat 上稳定运行员工信息管理系统。
