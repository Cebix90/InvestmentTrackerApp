package com.cebix.investmenttrackerapp.exceptions;

public class UserExistsException extends RuntimeException {
    @Override
    public String getMessage() {
        return "User with same email already exists. Please try with another email.";
    }
}
