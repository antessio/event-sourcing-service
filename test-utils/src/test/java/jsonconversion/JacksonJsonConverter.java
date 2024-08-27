package jsonconversion;

import java.io.IOException;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import antessio.eventsourcing.jsonconversion.JsonConverter;

public class JacksonJsonConverter implements JsonConverter {

    private final ObjectMapper objectMapper;

    public JacksonJsonConverter() {
        objectMapper = new ObjectMapper();
    }

    @Override
    public <T> T fromJson(String json, Class<? extends T> cls) {
        try {
            return objectMapper.readValue(json, cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> String toJson(T obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
