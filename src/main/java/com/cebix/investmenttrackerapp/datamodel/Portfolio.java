package com.cebix.investmenttrackerapp.datamodel;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Entity
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne()
    private CustomUser user;

    @ElementCollection
    @CollectionTable(name = "portfolio_stocks", joinColumns = @JoinColumn(name = "portfolio_id"))
    @MapKeyColumn(name = "stock_ticker")
    @Column(name = "amount_bought")
    private Map<String, Integer> stocks;

    private double overallValue;

    @ElementCollection
    @CollectionTable(name = "portfolio_historical_value", joinColumns = @JoinColumn(name = "portfolio_id"))
    @MapKeyColumn(name = "date")
    @Column(name = "value")
    private Map<LocalDate, Double> historicalValue;

    public Portfolio() {
    }

    public Portfolio(CustomUser user, Map<String, Integer> stocks, double overallValue, Map<LocalDate, Double> historicalValue) {
        this.user = user;
        this.stocks = stocks;
        this.overallValue = overallValue;
        this.historicalValue = historicalValue;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CustomUser getUser() {
        return user;
    }

    public void setUser(CustomUser user) {
        this.user = user;
    }

    public Map<String, Integer> getStocks() {
        return stocks;
    }

    public void setStocks(Map<String, Integer> stocks) {
        this.stocks = stocks;
    }

    public double getOverallValue() {
        return overallValue;
    }

    public void setOverallValue(double overallValue) {
        this.overallValue = overallValue;
    }

    public Map<LocalDate, Double> getHistoricalValue() {
        return historicalValue;
    }

    public void setHistoricalValue(Map<LocalDate, Double> historicalValue) {
        this.historicalValue = historicalValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Portfolio portfolio = (Portfolio) o;
        return id == portfolio.id && Double.compare(overallValue, portfolio.overallValue) == 0 && Objects.equals(user.getId(), portfolio.user.getId()) && Objects.equals(stocks, portfolio.stocks) && Objects.equals(historicalValue, portfolio.historicalValue);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + Objects.hashCode(user);
        result = 31 * result + Objects.hashCode(stocks);
        result = 31 * result + Double.hashCode(overallValue);
        result = 31 * result + Objects.hashCode(historicalValue);
        return result;
    }
}
