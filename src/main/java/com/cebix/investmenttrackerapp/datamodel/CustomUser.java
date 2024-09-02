package com.cebix.investmenttrackerapp.datamodel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class CustomUser {
    @Id
    @GeneratedValue
    private Long id;
    private String email;
    private String password;
    private String portfolio;

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomUser customUser = (CustomUser) o;
        return id == customUser.id && Objects.equals(email, customUser.email) && Objects.equals(password, customUser.password) && Objects.equals(portfolio, customUser.portfolio);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + Objects.hashCode(email);
        result = 31 * result + Objects.hashCode(password);
        result = 31 * result + Objects.hashCode(portfolio);
        return result;
    }
}
