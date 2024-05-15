package com.cebix.investmenttrackerapp.datamodel;

import java.time.LocalDate;

public class TechnicalIndicator {
    private String ticker;
    private String type;
    private double value;
    private LocalDate date;

    public TechnicalIndicator() { }

    public TechnicalIndicator(String ticker, String type, double value, LocalDate date) {
        this.ticker = ticker;
        this.type = type;
        this.value = value;
        this.date = date;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        return "TechnicalIndicator{" +
                "ticker='" + ticker + '\'' +
                ", type='" + type + '\'' +
                ", value=" + value +
                ", date=" + date +
                '}';
    }
}
