package com.example.employeesystem.util;

import com.example.employeesystem.annotation.Column;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用反射与 {@link Column} 注解将 JDBC {@link ResultSet} 转换为实体对象的工具类。
 */
public final class ResultSetMapper {

    private ResultSetMapper() {
    }

    public static <T> List<T> map(ResultSet resultSet, Class<T> targetType) throws SQLException {
        // 预先记录列名与索引的对应关系，减少循环内查找成本
        List<T> results = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        Map<String, Integer> columnIndexMap = new HashMap<>();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            columnIndexMap.put(metaData.getColumnLabel(i).toLowerCase(), i);
        }

        Field[] fields = targetType.getDeclaredFields();

        try {
            Constructor<T> constructor = targetType.getDeclaredConstructor();
            constructor.setAccessible(true);

            while (resultSet.next()) {
                T instance = constructor.newInstance();
                for (Field field : fields) {
                    String columnName = resolveColumnName(field);
                    Integer columnIndex = columnIndexMap.get(columnName.toLowerCase());
                    if (columnIndex == null) {
                        continue;
                    }
                    // 根据字段类型读取并转换 ResultSet 中的值
                    Object value = readValue(resultSet, columnIndex, field.getType());
                    if (value != null) {
                        field.setAccessible(true);
                        field.set(instance, value);
                    }
                }
                results.add(instance);
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to map ResultSet", e);
        }

        return results;
    }

    private static String resolveColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null) {
            return column.value();
        }
        // 默认使用字段名作为列名
        return field.getName();
    }

    private static Object readValue(ResultSet resultSet, int columnIndex, Class<?> targetType) throws SQLException {
        Object raw = resultSet.getObject(columnIndex);
        if (raw == null) {
            return null;
        }
        if (targetType.isAssignableFrom(raw.getClass())) {
            return raw;
        }
        // 针对常见日期、数字类型做兼容转换
        if (targetType == LocalDateTime.class) {
            if (raw instanceof Timestamp) {
                return ((Timestamp) raw).toLocalDateTime();
            }
            if (raw instanceof Date) {
                return new Timestamp(((Date) raw).getTime()).toLocalDateTime();
            }
        }
        if (targetType == LocalDate.class) {
            if (raw instanceof java.sql.Date) {
                return ((java.sql.Date) raw).toLocalDate();
            }
            if (raw instanceof Timestamp) {
                return ((Timestamp) raw).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
            if (raw instanceof Date) {
                Instant instant = Instant.ofEpochMilli(((Date) raw).getTime());
                return instant.atZone(ZoneId.systemDefault()).toLocalDate();
            }
        }
        if (targetType == Integer.class || targetType == int.class) {
            return ((Number) raw).intValue();
        }
        if (targetType == Long.class || targetType == long.class) {
            return ((Number) raw).longValue();
        }
        if (targetType == Double.class || targetType == double.class) {
            return ((Number) raw).doubleValue();
        }
        if (targetType == BigDecimal.class) {
            if (raw instanceof BigDecimal) {
                return raw;
            }
            return new BigDecimal(raw.toString());
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            if (raw instanceof Boolean) {
                return raw;
            }
            return ((Number) raw).intValue() != 0;
        }
        return raw;
    }
}
