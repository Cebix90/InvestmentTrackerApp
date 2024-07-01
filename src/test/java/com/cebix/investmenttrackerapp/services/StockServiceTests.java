package com.cebix.investmenttrackerapp.services;

import com.cebix.investmenttrackerapp.datamodel.Stock;
import com.cebix.investmenttrackerapp.exceptions.InvalidTickerException;
import com.cebix.investmenttrackerapp.handlers.StocksAPIHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockServiceTests {

    @Mock
    private StocksAPIHandler stocksAPIHandler;

    @InjectMocks
    private StockService stockService;

    private final String ticker = "AAPL";
    private final String date = "2024-06-28";

    @Test
    public void testRetrieveStockData_WithCorrectData_ReturnsCorrectStock() throws IOException {
        String stockJSON = new String(Files.readAllBytes(Paths.get("src/test/resources/services/validStock.json")));

        when(stocksAPIHandler.getStockData(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(stockJSON));

        Stock stock = stockService.retrieveStockData(ticker, date);

        assertNotNull(stock);
        assertEquals(ticker, stock.getTicker());
        assertEquals(210.62, stock.getValue());
        assertEquals(LocalDate.parse(date), stock.getDate());
    }

    @Test
    public void testRetrieveStockData_WithTickerAsNull_ThrowsInvalidTickerException() {
        assertThrows(InvalidTickerException.class, () -> stockService.retrieveStockData(null, date));
    }

    @Test
    public void testRetrieveStockData_WithTickerAsEmptyString_ThrowsInvalidTickerException() {
        assertThrows(InvalidTickerException.class, () -> stockService.retrieveStockData("", date));
    }

    @Test
    public void testRetrieveStockData_WithIncorrectDataFormat_ThrowsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> stockService.retrieveStockData(ticker, "06-28-2024"));
        assertThrows(DateTimeParseException.class, () -> stockService.retrieveStockData(ticker, "2024-6-28"));
        assertThrows(DateTimeParseException.class, () -> stockService.retrieveStockData(ticker, "6-28-2024"));
    }

    @Test
    public void testRetrieveStockData_WithInvalidTicker_ThrowsInvalidTickerException() throws IOException {
        String ticker = "asas";
        String stockJSON = new String(Files.readAllBytes(Paths.get("src/test/resources/services/invalidTicker.json")));

        when(stocksAPIHandler.getStockData(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(stockJSON));

        assertThrows(InvalidTickerException.class, () -> stockService.retrieveStockData(ticker, date));
    }

    @Test
    public void testRetrieveStockData_WithDateAsNull_ThrowsInvalidTickerException() throws IOException {
        String stockJSON = new String(Files.readAllBytes(Paths.get("src/test/resources/services/validStock.json")));

        when(stocksAPIHandler.getStockData(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(stockJSON));

        LocalDate mockToday = LocalDate.of(2024, 7, 1);
        try (MockedStatic<LocalDate> mock = Mockito.mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            mock.when(LocalDate::now).thenReturn(mockToday);

            Stock stock = stockService.retrieveStockData(ticker, null);

            assertNotNull(stock);
            assertEquals(ticker, stock.getTicker());
            assertEquals(210.62, stock.getValue());
            assertEquals(LocalDate.parse("2024-06-28"), stock.getDate());
        }
    }

    @Test
    public void testRetrieveStockData_WithDateAsEmptyString_ThrowsInvalidTickerException() throws IOException {
        String stockJSON = new String(Files.readAllBytes(Paths.get("src/test/resources/services/validStock.json")));

        when(stocksAPIHandler.getStockData(anyString(), anyString(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(Mono.just(stockJSON));

        LocalDate mockToday = LocalDate.of(2024, 6, 30);
        try (MockedStatic<LocalDate> mock = Mockito.mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
            mock.when(LocalDate::now).thenReturn(mockToday);

            Stock stock = stockService.retrieveStockData(ticker, "");

            assertNotNull(stock);
            assertEquals(ticker, stock.getTicker());
            assertEquals(210.62, stock.getValue());
            assertEquals(LocalDate.parse("2024-06-28"), stock.getDate());
        }
    }
}