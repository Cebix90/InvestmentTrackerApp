package com.cebix.investmenttrackerapp.databaseutils;

import com.cebix.investmenttrackerapp.datamodel.CustomUser;
import com.cebix.investmenttrackerapp.exceptions.UserAlreadyExistsException;
import com.cebix.investmenttrackerapp.exceptions.UserNotFoundException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.function.Consumer;

@Repository
public class CustomUserDAO {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SessionFactory sessionFactory = CustomUserSessionFactory.getCustomUserSessionFactory();

    public void saveUser(CustomUser customUser) {
        try (Session session = sessionFactory.openSession()) {
            if(!userExists(customUser)) {
                Transaction transaction = session.beginTransaction();
                customUser.setPassword(passwordEncoder.encode(customUser.getPassword()));
                session.merge(customUser);
                transaction.commit();
            } else {
                throw new UserAlreadyExistsException();
            }
        }
    }

    public CustomUser findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }

        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<CustomUser> userQuery = criteriaBuilder.createQuery(CustomUser.class);
            Root<CustomUser> userRoot = userQuery.from(CustomUser.class);
            userQuery.select(userRoot).where(criteriaBuilder.equal(userRoot.get("email"), email));
            CustomUser customUser = session.createQuery(userQuery).getSingleResultOrNull();
            return customUser;
        }
    }

    public void deleteUser(String customUserEmail) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            CustomUser customUserToDelete = findUserByEmail(customUserEmail);
            if (customUserToDelete != null) {
                session.remove(customUserToDelete);
                transaction.commit();
            } else {
                throw new UserNotFoundException();
            }
        }
    }

    public void updateUserEmail(String currentEmail, String newEmail) {
        updateUserField(currentEmail, user -> user.setEmail(newEmail), this::validateEmail, newEmail);
    }

    public void updateUserPassword(String email, String newPassword) {
        updateUserField(email, user -> user.setPassword(passwordEncoder.encode(newPassword)), this::validatePassword, newPassword);
    }

    public void updateUserPortfolio(String email, String newPortfolio) {
        updateUserField(email, user -> user.setPortfolio(newPortfolio), this::validatePortfolio, newPortfolio);
    }

    private <T> void updateUserField(String email, Consumer<CustomUser> fieldUpdater, Consumer<T> validator, T value) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            CustomUser user = findUserByEmail(email);

            if (user != null) {
                validator.accept(value);
                fieldUpdater.accept(user);
                session.merge(user);
                transaction.commit();
            } else {
                throw new NoResultException("User not found with email: " + email);
            }
        } catch (IllegalArgumentException | NoResultException e) {
            e.printStackTrace();
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password must not be null or empty");
        }
    }

    private void validatePortfolio(String portfolio) {
        if (portfolio == null || portfolio.trim().isEmpty()) {
            throw new IllegalArgumentException("Portfolio must not be null or empty");
        }
    }

    private boolean userExists(CustomUser customUser) {
        return findUserByEmail(customUser.getEmail()) != null;
    }
}
