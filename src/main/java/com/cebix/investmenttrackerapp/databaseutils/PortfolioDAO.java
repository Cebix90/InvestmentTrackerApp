package com.cebix.investmenttrackerapp.databaseutils;

import com.cebix.investmenttrackerapp.datamodel.CustomUser;
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
import java.util.function.Predicate;

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

            CustomUser managedUser = session.merge(portfolio.getUser());
            portfolio.setUser(managedUser);

            session.persist(portfolio);
            transaction.commit();
        }
    }

    public Portfolio findPortfolioByUserId(long customUserId) {
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

    public void deletePortfolio(long customUserId) {
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

    public Portfolio updatePortfolioStocksCollection(long customUserId, Map<String, Integer> newStocks) {
        return updatePortfolioField(customUserId, portfolio -> portfolio.setStocks(newStocks), this::validateStock, newStocks, "Stocks");
    }

    public Portfolio updatePortfolioOverallValue(long customUserId, double newOverallValue) {
        return updatePortfolioField(customUserId, portfolio -> portfolio.setOverallValue(newOverallValue), this::validateOverallValue, newOverallValue, "OverallValue");
    }

    public Portfolio updatePortfolioHistoricalValue(long customUserId, Map<LocalDate, Double> newHistoricalValues) {
        return updatePortfolioField(customUserId, portfolio -> portfolio.setHistoricalValue(newHistoricalValues), this::validateHistoricalValue, newHistoricalValues, "HistoricalValue");
    }

    private <T> Portfolio updatePortfolioField(long customUserId, Consumer<Portfolio> fieldUpdater, Predicate<T> validator, T value, String fieldName) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Portfolio portfolioToUpdate = findPortfolioByUserId(customUserId);

            if (portfolioToUpdate != null) {
                if (!validator.test(value)) {
                    throw new IllegalArgumentException(getValidationErrorMessage(fieldName, value));
                }
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

    private boolean validateStock(Map<String, Integer> stocks) {
        return stocks != null && !stocks.isEmpty();
    }

    private boolean validateOverallValue(double overallValue) {
        return overallValue >= 0;
    }

    private boolean validateHistoricalValue(Map<LocalDate, Double> historicalValues) {
        return historicalValues != null && !historicalValues.isEmpty();
    }

    private <T> String getValidationErrorMessage(String fieldName, T value) {
        if (value == null) {
            return fieldName + " cannot be null.";
        }

        if (value instanceof Map<?,?>) {
            if (((Map<?, ?>) value).isEmpty()) {
                return fieldName + " cannot be empty.";
            }
        }

        return fieldName + " cannot be less than zero.";
    }
}
