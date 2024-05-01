package com.cebix.investmenttrackerapp;

import com.cebix.investmenttrackerapp.handlers.StocksAPIHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class StocksAPIHandlerTests {
    @Test
    public void testGetStockData() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        StocksAPIHandler stocksAPIHandler = new StocksAPIHandler(restTemplate);

        String ticker = "AAPL";
        String multiplier = "1";
        String timespan = "day";
        String from = "2023-01-09";
        String to = "2023-01-09";
        String expectedResponse = TestHelper.getJsonResponseFromFile("stocks_API_handler_response.json");

        when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(String.class)))
                .thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        String actualResponse = stocksAPIHandler.getStockData(ticker, multiplier, timespan, from, to);

        assertEquals(expectedResponse, actualResponse);
    }
}
