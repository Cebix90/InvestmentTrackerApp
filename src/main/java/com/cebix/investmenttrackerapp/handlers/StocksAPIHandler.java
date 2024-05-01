package com.cebix.investmenttrackerapp.handlers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class StocksAPIHandler {
    private final String STOCKS_URL = "https://api.polygon.io/v2/aggs/ticker/";
    private final String API_KEY = "UA1AF1IBzgbxI5dsNxYrtXcJJdtal1GI";

    private final RestTemplate restTemplate;

    public StocksAPIHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getStockData(String ticker, String multiplier, String timespan, String from, String to) {
        String url = STOCKS_URL + ticker + "/range/" + multiplier + "/" + timespan + "/" + from + "/" + to + "?adjusted=true&sort=asc&limit=120&apiKey=" + API_KEY;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }
}
