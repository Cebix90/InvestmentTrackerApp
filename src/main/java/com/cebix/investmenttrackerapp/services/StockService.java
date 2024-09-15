package com.cebix.investmenttrackerapp.services;

import com.cebix.investmenttrackerapp.datamodel.Stock;
import com.cebix.investmenttrackerapp.exceptions.FutureDateException;
import com.cebix.investmenttrackerapp.exceptions.InvalidTickerException;
import com.cebix.investmenttrackerapp.handlers.StocksAPIHandler;
import com.cebix.investmenttrackerapp.mappers.StockMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class StockService {

    private final StocksAPIHandler stocksAPIHandler;

    @Autowired
    public StockService(StocksAPIHandler stocksAPIHandler) {
        this.stocksAPIHandler = stocksAPIHandler;
    }

    public Stock retrieveStockData(String ticker, String date) {
        if (ticker == null || ticker.isEmpty()) {
            throw new InvalidTickerException();
        }

        LocalDate selectedDate;
        if (date == null || date.isEmpty()) {
            selectedDate = LocalDate.now().minusDays(1);
        } else {
            selectedDate = LocalDate.parse(date);
        }

        if (selectedDate.isEqual(LocalDate.now()) || selectedDate.isAfter(LocalDate.now())) {
            throw new FutureDateException();
        }

        selectedDate = getLastWorkingDay(selectedDate);

        String selectedDateString = selectedDate.toString();

        String jsonData = stocksAPIHandler.getStockData(ticker, "1", "day", selectedDateString, selectedDateString, 1).block();

        JSONObject jsonObject = new JSONObject(jsonData);
        int queryCount = jsonObject.getInt("queryCount");
        int resultsCount = jsonObject.getInt("resultsCount");

        if (queryCount == 0 || resultsCount == 0) {
            throw new InvalidTickerException();
        }

        return StockMapper.mapJSONToStock(jsonData);
    }

    private LocalDate getLastWorkingDay(LocalDate date) {
        if(date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            date = date.minusDays(1);
        } else if(date.getDayOfWeek() == DayOfWeek.SUNDAY) {
             date = date.minusDays(2);
        }

        return date;
    }
}
