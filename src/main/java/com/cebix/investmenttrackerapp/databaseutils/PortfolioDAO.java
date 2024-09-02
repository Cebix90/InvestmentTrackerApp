package com.cebix.investmenttrackerapp.databaseutils;

import com.cebix.investmenttrackerapp.datamodel.Portfolio;
import com.cebix.investmenttrackerapp.exceptions.UserNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

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
}
