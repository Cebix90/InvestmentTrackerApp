package com.cebix.investmenttrackerapp.exceptions;

public class InvalidTickerException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Incorrect ticker or no results. Please try with another ticker.";
    }
}