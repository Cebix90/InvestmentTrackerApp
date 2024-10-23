package com.cebix.investmenttrackerapp.dtos;

import java.time.LocalDate;

public class StockDTO {
    private String ticker;
    private int amount;
    private LocalDate date;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}