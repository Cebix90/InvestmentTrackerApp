// src/main/java/com/cebix/investmenttrackerapp/controllers/SearchStockController.java
package com.cebix.investmenttrackerapp.controllers;

import com.cebix.investmenttrackerapp.datamodel.Stock;
import com.cebix.investmenttrackerapp.handlers.StocksAPIHandler;
import com.cebix.investmenttrackerapp.mappers.StockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Controller
public class SearchStockController {

    @Autowired
    private StocksAPIHandler stocksAPIHandler;

    @GetMapping("/searchStock")
    public String searchStockPage() {
        return "searchStock";
    }

    @GetMapping("/getStock")
    public String getStockData(@RequestParam String ticker,
                               @RequestParam(required = false) String date,
                               Model model) {
        System.out.println("Ticker: " + ticker);
        System.out.println("Date: " + date);

        if (date == null || date.isEmpty()) {
            date = LocalDate.now().minusDays(1).toString();
        }

        try {
            String jsonData = stocksAPIHandler.getStockData(ticker, "1", "day", date, date, 1).block();
            Stock stock = StockMapper.mapJSONToStock(jsonData);
            model.addAttribute("stock", stock);
        } catch (Exception e) {
            model.addAttribute("error", "Incorrect data. Please try again with a different ticker or date.");
        }

        return "searchStock";
    }
}
