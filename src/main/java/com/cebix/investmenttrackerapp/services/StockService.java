package com.cebix.investmenttrackerapp.services;

import com.cebix.investmenttrackerapp.datamodel.Stock;
import com.cebix.investmenttrackerapp.exceptions.InvalidTickerException;
import com.cebix.investmenttrackerapp.handlers.StocksAPIHandler;
import com.cebix.investmenttrackerapp.mappers.StockMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
public class StockService {

    private final StocksAPIHandler stocksAPIHandler;

    @Autowired
    public StockService(StocksAPIHandler stocksAPIHandler) {
        this.stocksAPIHandler = stocksAPIHandler;
    }

    public Stock getStockData(String ticker, String date) {
        if (ticker == null || ticker.isEmpty()) {
            throw new InvalidTickerException();
        }

        LocalDate selectedDate;
        if (date == null || date.isEmpty()) {
            selectedDate = getLastWorkingDay();
        } else {
            try {
                selectedDate = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                throw new DateTimeParseException("Invalid date format: " + e.getParsedString(), e.getParsedString(), e.getErrorIndex());
            }
        }

        String selectedDateString = selectedDate.toString();

        String jsonData = stocksAPIHandler.getStockDataFromApi(ticker, "1", "day", selectedDateString, selectedDateString, 1).block();

        JSONObject jsonObject = new JSONObject(jsonData);
        int queryCount = jsonObject.getInt("queryCount");
        int resultsCount = jsonObject.getInt("resultsCount");

        if (queryCount == 0 || resultsCount == 0) {
            throw new InvalidTickerException();
        }

        return StockMapper.mapJSONToStock(jsonData);
    }

    private LocalDate getLastWorkingDay() {
        LocalDate date = LocalDate.now().minusDays(1);

        while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }

        return date;
    }
}
