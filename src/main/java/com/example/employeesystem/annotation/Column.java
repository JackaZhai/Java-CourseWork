package com.example.employeesystem.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Maps an entity field to a database column.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String value();
}
