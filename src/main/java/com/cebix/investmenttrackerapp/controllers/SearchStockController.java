// src/main/java/com/cebix/investmenttrackerapp/controllers/SearchStockController.java
package com.cebix.investmenttrackerapp.controllers;

import com.cebix.investmenttrackerapp.datamodel.Stock;
import com.cebix.investmenttrackerapp.exceptions.FutureDateException;
import com.cebix.investmenttrackerapp.exceptions.InvalidTickerException;
import com.cebix.investmenttrackerapp.handlers.StocksAPIHandler;
import com.cebix.investmenttrackerapp.services.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeParseException;

@Controller
public class SearchStockController {

    private final StocksAPIHandler stocksAPIHandler = new StocksAPIHandler();
    private final StockService stockService = new StockService(stocksAPIHandler);

    @GetMapping("/searchStock")
    public String searchStockPage() {
        return "searchStock";
    }

    @GetMapping("/getStock")
    public String getStockData(@RequestParam String ticker,
                               @RequestParam(required = false) String date,
                               Model model) {
        try {
            Stock stock = stockService.getStockData(ticker, date);
            model.addAttribute("stock", stock);
        }  catch (InvalidTickerException | FutureDateException | DateTimeParseException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
        }

        return "searchStock";
    }
}
