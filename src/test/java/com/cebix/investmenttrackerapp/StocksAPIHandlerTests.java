package com.cebix.investmenttrackerapp;

import com.cebix.investmenttrackerapp.handlers.StocksAPIHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StocksAPIHandlerTests {
    @Test
    public void testGetStockData() throws JsonProcessingException {
        String ticker = "AAPL";
        String multiplier = "1";
        String timespan = "day";
        String from = "2023-01-09";
        String to = "2023-01-09";
        String expectedResponse = TestHelper.getJsonResponseFromFile("stocks_API_handler_response.json");

        StocksAPIHandler stocksAPIHandler = new StocksAPIHandler(WebClient.builder());
        Mono<String> actualResponseMono = stocksAPIHandler.getStockData(ticker, multiplier, timespan, from, to);

        String actualResponse = actualResponseMono.block();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> expectedResponseMap = objectMapper.readValue(expectedResponse, new TypeReference<>() {});
        Map<String, Object> actualResponseMap = objectMapper.readValue(actualResponse, new TypeReference<>() {});


        List<Map<String, Object>> actualResults = (List<Map<String, Object>>) actualResponseMap.get("results");
        for (Map<String, Object> result : actualResults) {
            BigDecimal actualVDecimal = new BigDecimal(result.get("v").toString());
            Integer actualV = actualVDecimal.intValue();
            result.put("v", actualV);
        }

        expectedResponseMap.remove("request_id");
        actualResponseMap.remove("request_id");

        assertEquals(expectedResponseMap, actualResponseMap);
    }

    @Test
    public void testGetStockData_throwsError_whenTickerIsEmpty() {
        String ticker = "";
        String multiplier = "1";
        String timespan = "day";
        String from = "2023-01-09";
        String to = "2023-01-09";

        StocksAPIHandler stocksAPIHandler = new StocksAPIHandler(WebClient.builder());

        Mono<String> actualResponseMono = stocksAPIHandler.getStockData(ticker, multiplier, timespan, from, to);

        StepVerifier.create(actualResponseMono)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("Ticker cannot be empty"))
                .verify();
    }

    @Test
    public void testGetStockData_throwsError_whenTickerIsNull() {
        String ticker = null;
        String multiplier = "1";
        String timespan = "day";
        String from = "2023-01-09";
        String to = "2023-01-09";

        StocksAPIHandler stocksAPIHandler = new StocksAPIHandler(WebClient.builder());

        Mono<String> actualResponseMono = stocksAPIHandler.getStockData(ticker, multiplier, timespan, from, to);

        StepVerifier.create(actualResponseMono)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("Ticker cannot be empty"))
                .verify();
    }

    @Test
    public void testGetStockData_throwsError_whenFromParameterIsSmallerThanParameterTo() {
        String ticker = "AAPL";
        String multiplier = "1";
        String timespan = "day";
        String from = "2023-01-19";
        String to = "2023-01-09";

        StocksAPIHandler stocksAPIHandler = new StocksAPIHandler(WebClient.builder());

        Mono<String> actualResponseMono = stocksAPIHandler.getStockData(ticker, multiplier, timespan, from, to);

        StepVerifier.create(actualResponseMono)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("The parameter 'to' cannot be a time that occurs before 'from'"))
                .verify();
    }
}
