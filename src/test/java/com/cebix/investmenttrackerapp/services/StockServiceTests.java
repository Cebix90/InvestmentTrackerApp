package com.cebix.investmenttrackerapp.services;

import com.cebix.investmenttrackerapp.datamodel.Stock;
import com.cebix.investmenttrackerapp.exceptions.InvalidTickerException;
import com.cebix.investmenttrackerapp.handlers.StocksAPIHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockServiceTests {

    @Mock
    private StocksAPIHandler stocksAPIHandler;

    @InjectMocks
    private StockService stockService;

    @Test
    public void testGetStockData_WithCorrectData_ReturnsCorrectStock() throws IOException {
        String ticker = "AAPL";
        String date = "2023-01-09";
        String stockJSON = new String(Files.readAllBytes(Paths.get("src/test/resources/mappers/stock.json")));

        when(stocksAPIHandler.getStockDataFromApi(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(stockJSON));

        Stock stock = stockService.getStockData(ticker, date);

        assertNotNull(stock);
        assertEquals(ticker, stock.getTicker());
        assertEquals(130.15, stock.getValue());
        assertEquals(LocalDate.parse(date), stock.getDate());
    }

    @Test
    public void testGetStockData_WithTickerAsNull_ThrowsInvalidTickerException() {
        String date = "2023-01-09";

        assertThrows(InvalidTickerException.class, () -> stockService.getStockData(null, date));
    }

    @Test
    public void testGetStockData_WithTickerAsEmptyString_ThrowsInvalidTickerException() {
        String date = "2023-01-09";

        assertThrows(InvalidTickerException.class, () -> stockService.getStockData("", date));
    }

    @Test
    public void testGetStockData_WithIncorrectDataFormat_ThrowsDateTimeParseException() {
        String ticker = "AAPL";

        assertThrows(DateTimeParseException.class, () -> stockService.getStockData(ticker, "09-01-2023"));
        assertThrows(DateTimeParseException.class, () -> stockService.getStockData(ticker, "2023-1-9"));
        assertThrows(DateTimeParseException.class, () -> stockService.getStockData(ticker, "1-9-2023"));
    }

    @Test
    public void testGetStockData_WithInvalidTicker_ThrowsInvalidTickerException() throws IOException {
        String ticker = "asas";
        String date = "2023-01-09";
        String stockJSON = new String(Files.readAllBytes(Paths.get("src/test/resources/invalidTicker.json")));

        when(stocksAPIHandler.getStockDataFromApi(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(stockJSON));

        assertThrows(InvalidTickerException.class, () -> stockService.getStockData(ticker, date));
    }


}