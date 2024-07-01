package com.cebix.investmenttrackerapp.exceptions;

public class FutureDateException extends RuntimeException {
    @Override
    public String getMessage() {
        return "You can only select a date at least one day in the past.";
    }
}