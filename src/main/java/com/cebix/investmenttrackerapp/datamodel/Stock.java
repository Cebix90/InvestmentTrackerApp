package com.cebix.investmenttrackerapp.datamodel;

import java.time.LocalDate;

public class Stock {
    private String ticker;
    private double value;
    private LocalDate date;

    public Stock() {
    }

    public Stock(String ticker, double value, LocalDate date) {
        this.ticker = ticker;
        this.value = value;
        this.date = date;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "ticker='" + ticker + '\'' +
                ", value=" + value +
                ", date=" + date +
                '}';
    }
}
