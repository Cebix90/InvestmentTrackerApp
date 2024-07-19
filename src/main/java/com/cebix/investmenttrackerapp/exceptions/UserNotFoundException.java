package com.cebix.investmenttrackerapp.exceptions;

public class UserNotFoundException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Email was not found. Please try different email.";
    }
}
