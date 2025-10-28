package com.example.employeesystem.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 指定实体字段对应的数据库列名，在 ResultSet 映射时使用。
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String value();
}
