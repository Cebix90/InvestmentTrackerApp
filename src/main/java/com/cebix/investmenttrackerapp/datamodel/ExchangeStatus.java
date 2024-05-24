package com.cebix.investmenttrackerapp.datamodel;

public class ExchangeStatus {
    private String exchange;
    private boolean isOpen;

    public ExchangeStatus(String exchange, boolean isOpen) {
        this.exchange = exchange;
        this.isOpen = isOpen;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Override
    public String toString() {
        return "ExchangeStatus{" +
                "exchange='" + exchange + '\'' +
                ", isOpen=" + isOpen +
                '}';
    }
}
