package com.cebix.investmenttrackerapp.services;

import com.cebix.investmenttrackerapp.datamodel.Stock;
import com.cebix.investmenttrackerapp.exceptions.DateOrderException;
import com.cebix.investmenttrackerapp.exceptions.FutureDateException;
import com.cebix.investmenttrackerapp.exceptions.InvalidDateException;
import com.cebix.investmenttrackerapp.exceptions.InvalidTickerException;
import com.cebix.investmenttrackerapp.handlers.StocksAPIHandler;
import com.cebix.investmenttrackerapp.mappers.StockMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Service
public class StockService {

    @Autowired
    private StocksAPIHandler stocksAPIHandler;

    public Stock getStockData(String ticker, String date) throws Exception {
        if (date == null || date.isEmpty()) {
            date = LocalDate.now().minusDays(1).toString();
        }

        try {
            LocalDate fromDate = LocalDate.parse(date);
            LocalDate toDate = LocalDate.parse(date);
            LocalDate currentDate = LocalDate.now();

            if (fromDate.isAfter(toDate)) {
                throw new DateOrderException("The parameter 'to' cannot be a time that occurs before 'from'");
            }

            if (fromDate.isAfter(currentDate) || fromDate.equals(currentDate)) {
                throw new FutureDateException("You can only select a date at least one day in the past.");
            }
        } catch (DateTimeParseException e) {
            throw new InvalidDateException("Invalid date format");
        }

        String jsonData = stocksAPIHandler.getStockData(ticker, "1", "day", date, date, 1).block();

        JSONObject jsonObject = new JSONObject(jsonData);
        int queryCount = jsonObject.getInt("queryCount");
        int resultsCount = jsonObject.getInt("resultsCount");

        if (queryCount == 0 || resultsCount == 0) {
            throw new InvalidTickerException("Incorrect ticker or no results. Please try with another ticker.");
        }

        return StockMapper.mapJSONToStock(jsonData);
    }
}
