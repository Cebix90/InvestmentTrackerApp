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
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(portfolio);
            transaction.commit();

        }
    }

    public Portfolio findPortfolioByUserEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }

        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Portfolio> portfolioCriteriaQuery = criteriaBuilder.createQuery(Portfolio.class);
            Root<Portfolio> portfolioRoot = portfolioCriteriaQuery.from(Portfolio.class);
            portfolioCriteriaQuery.select(portfolioRoot).where(criteriaBuilder.equal(portfolioRoot.get("email"), email));
            Portfolio portfolio = session.createQuery(portfolioCriteriaQuery).getSingleResultOrNull();
            return portfolio;
        }
    }

    public void deletePortfolio(String portfolioCustomUserEmail) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Portfolio customUserToDelete = findPortfolioByUserEmail(portfolioCustomUserEmail);
            if (customUserToDelete != null) {
                session.remove(customUserToDelete);
                transaction.commit();
            } else {
                throw new UserNotFoundException();
            }
        }
    }
}
