-- 数据库表结构定义
CREATE TABLE account (
    id VARCHAR(32) PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    last_login_time DATETIME NULL
);

CREATE TABLE option_item (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    category VARCHAR(64) NOT NULL,
    value VARCHAR(64) NOT NULL,
    remark VARCHAR(255)
);

CREATE TABLE employee (
    id VARCHAR(32) PRIMARY KEY,
    employee_code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(8) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    hire_date DATE NOT NULL,
    job_option_id VARCHAR(32) NOT NULL,
    salary DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_employee_job FOREIGN KEY (job_option_id) REFERENCES option_item (id)
);

-- 初始化管理员账号（密码为明文示例，可在部署时加密）
INSERT INTO account (id, username, password) VALUES ('admin-id', 'admin', 'admin123');
