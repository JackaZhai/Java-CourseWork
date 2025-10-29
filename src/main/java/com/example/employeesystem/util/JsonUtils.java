package com.example.employeesystem.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

/**
 * 简单的 JSON 工具，封装 Jackson 的常用配置以复用。
 */
public final class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private JsonUtils() {
    }

    public static ObjectMapper mapper() {
        return OBJECT_MAPPER;
    }

    public static <T> T read(InputStream inputStream, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(inputStream, clazz);
    }

    public static String write(Object value) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(value);
    }

    public static void write(Writer writer, Object value) throws IOException {
        OBJECT_MAPPER.writeValue(writer, value);
    }
}
