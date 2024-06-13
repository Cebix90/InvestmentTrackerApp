// src/main/java/com/cebix/investmenttrackerapp/controllers/SearchStockController.java
package com.cebix.investmenttrackerapp.controllers;

import com.cebix.investmenttrackerapp.datamodel.Stock;
import com.cebix.investmenttrackerapp.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchStockController {

    @Autowired
    private StockService stockService;

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
        } catch (Exception e) {
            model.addAttribute("error", "Incorrect data. Please try again with a different ticker or date.");
        }

        return "searchStock";
    }
}
