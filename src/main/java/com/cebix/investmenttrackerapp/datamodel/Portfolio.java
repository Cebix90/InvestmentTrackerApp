package com.cebix.investmenttrackerapp.datamodel;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Entity
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade=CascadeType.ALL)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

        if (!Objects.equals(id, portfolio.id)) return false;
        if (Double.compare(portfolio.overallValue, overallValue) != 0) return false;
        if (!user.equals(portfolio.user)) return false;
        if (!stocks.equals(portfolio.stocks)) return false;
        return historicalValue.equals(portfolio.historicalValue);
    }

    @Override
    public int hashCode() {
        int result;
        result = id.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + stocks.hashCode();
        result = 31 * result + Double.hashCode(overallValue);
        result = 31 * result + historicalValue.hashCode();
        return result;
    }
}
