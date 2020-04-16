package com.github.tornaia.mybadcast.server.yaml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlReader {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> read(InputStream inputStream) {
        Map<String, List<Map<String, Map<String, Object>>>> yamlAsMap = readYamlAsMap(inputStream);
        return extractVariables(yamlAsMap);
    }

    private Map<String, List<Map<String, Map<String, Object>>>> readYamlAsMap(InputStream inputStream) {
        try {
            Yaml yaml = new Yaml();
            Map<String, List<Map<String, Map<String, Object>>>> m = yaml.load(inputStream);
            inputStream.close();
            return m;
        } catch (FileNotFoundException | NoSuchFileException e) {
            throw new IllegalStateException("Please create your manifest file under " + inputStream, e);
        } catch (IOException e) {
            throw new IllegalStateException("Must not happen", e);
        }
    }

    private Map<String, String> extractVariables(Map<String, List<Map<String, Map<String, Object>>>> map) {
        List<Map<String, Map<String, Object>>> applications = map.get("applications");
        Map<String, Map<String, Object>> applicationsMap = applications.get(0);
        Map<String, Object> envMap = applicationsMap.get("env");

        Map<String, String> ret = new HashMap<>();
        envMap.forEach((key, value) -> {
            if (value instanceof String) {
                ret.put(key, (String) value);
                return;
            }
            String valueAsString;
            try {
                valueAsString = objectMapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Failed to parse yaml file", e);
            }
            ret.put(key, valueAsString);
        });
        return ret;
    }
}
