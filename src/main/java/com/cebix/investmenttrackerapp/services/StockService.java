package com.cebix.investmenttrackerapp.services;

import com.cebix.investmenttrackerapp.datamodel.Stock;
import com.cebix.investmenttrackerapp.handlers.StocksAPIHandler;
import com.cebix.investmenttrackerapp.mappers.StockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class StockService {

    @Autowired
    private StocksAPIHandler stocksAPIHandler;

    public Stock getStockData(String ticker, String date) {
        if (date == null || date.isEmpty()) {
            date = LocalDate.now().minusDays(1).toString();
        }

        String jsonData = stocksAPIHandler.getStockData(ticker, "1", "day", date, date, 1).block();
        return StockMapper.mapJSONToStock(jsonData);
    }
}
