package com.cebix.investmenttrackerapp.databaseutils;

import com.cebix.investmenttrackerapp.datamodel.Portfolio;
import com.cebix.investmenttrackerapp.exceptions.UserNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Consumer;

public class PortfolioDAO {
    private final SessionFactory sessionFactory;

    public PortfolioDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void savePortfolio(Portfolio portfolio) {
        if (portfolio.getUser() == null) {
            throw new IllegalArgumentException("Portfolio must be associated with a user.");
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(portfolio);
            transaction.commit();
        }
    }

    public Portfolio findPortfolioByUserId(Long customUserId) {
        if (customUserId == null) {
            throw new UserNotFoundException();
        }

        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Portfolio> portfolioCriteriaQuery = criteriaBuilder.createQuery(Portfolio.class);
            Root<Portfolio> portfolioRoot = portfolioCriteriaQuery.from(Portfolio.class);
            portfolioCriteriaQuery.select(portfolioRoot).where(criteriaBuilder.equal(portfolioRoot.get("user").get("id"), customUserId));
            Portfolio portfolio = session.createQuery(portfolioCriteriaQuery).getSingleResultOrNull();

            if (portfolio != null) {
                Hibernate.initialize(portfolio.getStocks());
                Hibernate.initialize(portfolio.getHistoricalValue());
            }

            return portfolio;
        }
    }

    public void deletePortfolio(Long customUserId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Portfolio portfolioToDelete = findPortfolioByUserId(customUserId);
            if (portfolioToDelete != null) {
                session.remove(portfolioToDelete);
                transaction.commit();
            } else {
                throw new UserNotFoundException();
            }
        }
    }

    public Portfolio updatePortfolioStocksCollection(Long customUserId, Map<String, Integer> newStocks) {
        return updatePortfolioField(customUserId, portfolio -> portfolio.setStocks(newStocks), this::validateStock, newStocks);
    }

    public Portfolio updatePortfolioOverallValue(Long customUserId, double newOverallValue) {
        return updatePortfolioField(customUserId, portfolio -> portfolio.setOverallValue(newOverallValue), this::validateOverallValue, newOverallValue);
    }

    public Portfolio updatePortfolioHistoricalValue(Long customUserId, Map<LocalDate, Double> newHistoricalValues) {
        return updatePortfolioField(customUserId, portfolio -> portfolio.setHistoricalValue(newHistoricalValues), this::validateHistoricalValue, newHistoricalValues);
    }

    private <T> Portfolio updatePortfolioField(Long customUserId, Consumer<Portfolio> fieldUpdater, Consumer<T> validator, T value) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Portfolio portfolioToUpdate = findPortfolioByUserId(customUserId);

            if (portfolioToUpdate != null) {
                validator.accept(value);
                fieldUpdater.accept(portfolioToUpdate);
                session.merge(portfolioToUpdate);
                transaction.commit();
                return portfolioToUpdate;
            } else {
                throw new UserNotFoundException();
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private void validateStock(Map<String, Integer> stocks) {
        if (stocks == null || stocks.isEmpty()) {
            throw new IllegalArgumentException("Stock must not be null or empty");
        }
    }

    private void validateOverallValue(double overallValue) {
        if (overallValue <= 0) {
            throw new IllegalArgumentException("Overall value cannot be less than zero");
        }
    }

    private void validateHistoricalValue(Map<LocalDate, Double> historicalValues) {
        if (historicalValues == null || historicalValues.isEmpty()) {
            throw new IllegalArgumentException("Historical Values must not be null or empty");
        }
    }
}
