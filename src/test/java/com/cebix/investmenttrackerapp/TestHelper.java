package com.cebix.investmenttrackerapp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class TestHelper {
    public static String getJsonResponseFromFile(String filename) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = "";
        try {
            InputStream inputStream = TestHelper.class.getResourceAsStream("/" + filename);
            if (inputStream != null) {
                jsonResponse = objectMapper.readTree(inputStream).toString();
            } else {
                System.err.println("File was not found: " + filename);
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading the JSON file: " + e.getMessage());
            e.printStackTrace();
        }
        return jsonResponse;
    }
}
