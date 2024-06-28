package com.cebix.investmenttrackerapp.services;

import com.cebix.investmenttrackerapp.datamodel.Stock;
import com.cebix.investmenttrackerapp.exceptions.FutureDateException;
import com.cebix.investmenttrackerapp.exceptions.InvalidTickerException;
import com.cebix.investmenttrackerapp.handlers.StocksAPIHandler;
import com.cebix.investmenttrackerapp.mappers.StockMapper;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
public class StockService {

    private final StocksAPIHandler stocksAPIHandler = new StocksAPIHandler();

    public Stock getStockData(String ticker, String date) {
        if (ticker == null || ticker.isEmpty()) {
            throw new InvalidTickerException();
        }

        if (date == null || date.isEmpty()) {
            date = LocalDate.now().minusDays(1).toString();
        }

        try {
            LocalDate fromDate = LocalDate.parse(date);
            LocalDate currentDate = LocalDate.now();

            if (fromDate.isAfter(currentDate) || fromDate.equals(currentDate)) {
                throw new FutureDateException();
            }
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Invalid date format: " + e.getParsedString(), e.getParsedString(), e.getErrorIndex());
        }

        String jsonData = stocksAPIHandler.getStockDataFromApi(ticker, "1", "day", date, date, 1).block();

        JSONObject jsonObject = new JSONObject(jsonData);
        int queryCount = jsonObject.getInt("queryCount");
        int resultsCount = jsonObject.getInt("resultsCount");

        if (queryCount == 0 || resultsCount == 0) {
            throw new InvalidTickerException();
        }

        return StockMapper.mapJSONToStock(jsonData);
    }
}
