package org.avni_integration_service.migrator.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class ObjectJsonMapper {
    private static Logger logger = Logger.getLogger(ObjectJsonMapper.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String writeValueAsString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(String json, Class<T> klass) {
        try {
            return objectMapper.readValue(json, klass);
        } catch (JsonProcessingException e) {
            logger.error(json);
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(File jsonFile, Class<T> klass) {
        try {
            return objectMapper.readValue(jsonFile, klass);
        } catch (Exception e) {
            logger.error(jsonFile);
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(String json, TypeReference typeReference) {
        try {
            return (T) objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
