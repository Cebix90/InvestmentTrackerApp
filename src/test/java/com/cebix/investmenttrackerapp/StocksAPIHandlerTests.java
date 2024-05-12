package com.cebix.investmenttrackerapp;

import com.cebix.investmenttrackerapp.handlers.StocksAPIHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

@SpringBootTest
public class StocksAPIHandlerTests {
    @Autowired
    private StocksAPIHandler stocksAPIHandler;

    @Test
    public void testGetStockDataIfSuccess() {
        String stockTicker = "AAPL";
        String multiplier = "1";
        String timespan = "day";
        String from = "2024-01-01";
        String to = "2024-12-31";

        StepVerifier.create(stocksAPIHandler.getStockData(stockTicker, multiplier, timespan, from, to))
                .expectNextMatches(response -> response != null && !response.isEmpty())
                .verifyComplete();
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

    @Test
    public void testGetStockData_throwsError403_whenDateIsGraterThanToday() {
        String stockTicker = "AAPL";
        String multiplier = "1";
        String timespan = "day";
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String from = tomorrow.toString();
        String to = tomorrow.toString();

        StepVerifier.create(stocksAPIHandler.getStockData(stockTicker, multiplier, timespan, from, to))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("Error occurred with status code: 403"))
                .verify();
    }
}
