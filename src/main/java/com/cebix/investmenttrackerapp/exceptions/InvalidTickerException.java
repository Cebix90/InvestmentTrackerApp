package com.cebix.investmenttrackerapp.exceptions;

public class InvalidTickerException extends RuntimeException {
    public InvalidTickerException(String message) {
        super(message);
    }
}