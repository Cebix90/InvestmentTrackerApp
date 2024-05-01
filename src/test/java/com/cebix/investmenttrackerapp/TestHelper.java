package com.cebix.investmenttrackerapp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Paths;

public class TestHelper {
    public static String getJsonResponseFromFile(String filename) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = "";
        try {
            jsonResponse = objectMapper.readTree(Paths.get(filename).toFile()).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }
}
