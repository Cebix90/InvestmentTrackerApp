package com.cebix.investmenttrackerapp.dtos;

import com.cebix.investmenttrackerapp.security.passwords.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class RegisterUserDTO {
    @NotEmpty(message = "Email can not be empty.")
    @Email(message = "Please provide correct email format.")
    private String email;

    @NotEmpty(message = "Password can not be empty.")
    @ValidPassword
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
