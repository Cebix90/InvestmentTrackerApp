package com.cebix.investmenttrackerapp.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Email already in use. Please try different email.";
    }
}
