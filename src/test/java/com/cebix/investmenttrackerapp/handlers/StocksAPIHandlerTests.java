package com.cebix.investmenttrackerapp.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.LocalDate;

@SpringBootTest
public class StocksAPIHandlerTests {
    @Autowired
    private StocksAPIHandler stocksAPIHandler;

    @Test
    public void testGetStockData_ifSuccess() {
        String stockTicker = "AAPL";
        String multiplier = "1";
        String timespan = "day";
        String from = "2024-01-01";
        String to = "2024-12-31";
        int limit = 120;

        StepVerifier.create(stocksAPIHandler.getStockData(stockTicker, multiplier, timespan, from, to, limit))
                .expectNextMatches(response -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode responseJson = objectMapper.readTree(response);
                        JsonNode queryCountNode = responseJson.get("queryCount");
                        JsonNode resultsCountNode = responseJson.get("resultsCount");
                        return response != null && !response.isEmpty() &&
                                queryCountNode.asInt() != 0 && resultsCountNode.asInt() != 0;
                    } catch (IOException e) {
                        return false;
                    }
                })
                .verifyComplete();
    }

    @Test
    public void testGetStockData_throwsError_whenFromParameterIsSmallerThanParameterTo() {
        String stockTicker = "AAPL";
        String multiplier = "1";
        String timespan = "day";
        String from = "2023-01-19";
        String to = "2023-01-09";
        int limit = 120;

        StepVerifier.create(stocksAPIHandler.getStockData(stockTicker, multiplier, timespan, from, to, limit))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("The parameter 'to' cannot be a time that occurs before 'from'"))
                .verify();
    }

    @Test
    public void testGetStockData_throwsError403_whenDateIsGraterThanToday() {
        String stockTicker = "AAPL";
        String multiplier = "1";
        String timespan = "day";
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String from = tomorrow.toString();
        String to = tomorrow.toString();
        int limit = 120;

        StepVerifier.create(stocksAPIHandler.getStockData(stockTicker, multiplier, timespan, from, to, limit))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("Error occurred with status code: 403"))
                .verify();
    }
}
