// src/main/java/com/cebix/investmenttrackerapp/controllers/SearchStockController.java
package com.cebix.investmenttrackerapp.controllers;

import com.cebix.investmenttrackerapp.datamodel.Stock;
import com.cebix.investmenttrackerapp.exceptions.FutureDateException;
import com.cebix.investmenttrackerapp.exceptions.InvalidTickerException;
import com.cebix.investmenttrackerapp.services.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchStockController {

    private final StockService stockService = new StockService();

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
        }  catch (InvalidTickerException | FutureDateException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
        }

        return "searchStock";
    }
}
