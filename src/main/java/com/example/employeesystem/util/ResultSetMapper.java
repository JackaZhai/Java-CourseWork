package com.example.employeesystem.util;

import com.example.employeesystem.annotation.Column;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps JDBC {@link ResultSet} rows to strongly typed objects using reflection and annotations.
 */
public final class ResultSetMapper {

    private ResultSetMapper() {
    }

    public static <T> List<T> map(ResultSet resultSet, Class<T> targetType) throws SQLException {
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
        if (targetType == LocalDateTime.class) {
            if (raw instanceof Timestamp) {
                return ((Timestamp) raw).toLocalDateTime();
            }
            if (raw instanceof Date) {
                return new Timestamp(((Date) raw).getTime()).toLocalDateTime();
            }
        }
        if (targetType == LocalDate.class) {
            if (raw instanceof Date) {
                return ((Date) raw).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }
            if (raw instanceof Timestamp) {
                return ((Timestamp) raw).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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
