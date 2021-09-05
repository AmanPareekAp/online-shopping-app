package com.online_shopping.service.product;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;

public class JsonConverter implements AttributeConverter<Map<String, String>, String> {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Logger logger;

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(Map<String, String> metadataFieldValuesMap) {

        String metadataFieldValuesMapJson = null;
        try {
            metadataFieldValuesMapJson = objectMapper.writeValueAsString(metadataFieldValuesMap);
        } catch (final JsonProcessingException e) {
            throw e;
        }

        return metadataFieldValuesMapJson;
    }


    @SneakyThrows
    @Override
    public Map<String, String> convertToEntityAttribute(String metadataFieldValuesMapJson) {

        Map<String, String> metadataFieldValuesMap = null;
        try {
            metadataFieldValuesMap = objectMapper.readValue(metadataFieldValuesMapJson, Map.class);
        } catch (final IOException e) {
            throw e;
        }

        return metadataFieldValuesMap;
    }

}
