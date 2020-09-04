package com.iris.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Author: zfl
 * @Date: 2020/7/17 15:45
 * @Version: 1.0.0
 */
public class JsonUtils {

    public static ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtils() {
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJsonStr(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T toObject(String json, Class<T> objType) {
        try {
            return objectMapper.readValue(json, objType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
