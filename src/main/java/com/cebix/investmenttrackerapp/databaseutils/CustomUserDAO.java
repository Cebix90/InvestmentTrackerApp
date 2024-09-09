package com.cebix.investmenttrackerapp.databaseutils;

import com.cebix.investmenttrackerapp.datamodel.CustomUser;
import com.cebix.investmenttrackerapp.datamodel.Portfolio;
import com.cebix.investmenttrackerapp.exceptions.UserAlreadyExistsException;
import com.cebix.investmenttrackerapp.exceptions.UserNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CustomUserDAO {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SessionFactory sessionFactory;

    public CustomUserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveUser(CustomUser customUser) {
        try (Session session = sessionFactory.openSession()) {
            if(!userExists(customUser)) {
                Transaction transaction = session.beginTransaction();
                customUser.setPassword(passwordEncoder.encode(customUser.getPassword()));
                session.persist(customUser);
                session.flush();
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
            return session.createQuery(userQuery).getSingleResultOrNull();
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

    public CustomUser updateUserEmail(String currentEmail, String newEmail) {
        return updateUserField(currentEmail, user -> user.setEmail(newEmail), this::validateEmail, newEmail, "Email");
    }

    public CustomUser updateUserPassword(String email, String newPassword) {
        return updateUserField(email, user -> user.setPassword(passwordEncoder.encode(newPassword)), this::validatePassword, newPassword, "Password");
    }

    public CustomUser updateUserPortfolio(String email, Portfolio newPortfolio) {
        return updateUserField(email, user -> user.setPortfolio(newPortfolio), this::validatePortfolio, newPortfolio, "Portfolio");
    }

    private <T> CustomUser updateUserField(String email, Consumer<CustomUser> fieldUpdater, Predicate<T> validator, T value, String fieldName) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            CustomUser customUserToUpdate = findUserByEmail(email);

            if (customUserToUpdate != null) {
                if (!validator.test(value)) {
                    throw new IllegalArgumentException(getValidationErrorMessage(fieldName, value));
                }
                fieldUpdater.accept(customUserToUpdate);
                session.merge(customUserToUpdate);
                transaction.commit();
                return customUserToUpdate;
            } else {
                throw new UserNotFoundException();
            }
        }
    }

    private boolean validateEmail(String email) {
        return email != null && !email.trim().isEmpty();
    }

    private boolean validatePassword(String password) {
        return password != null && !password.trim().isEmpty();
    }

    private boolean validatePortfolio(Portfolio portfolio) {
        return portfolio != null;
    }

    private <T> String getValidationErrorMessage(String fieldName, T value) {
        if (value instanceof String) {
            if (value.equals("")) {
                return fieldName + " cannot be empty.";
            } else {
                return fieldName + " cannot be null.";
            }
        }

        return fieldName + " cannot be null.";
    }

    private boolean userExists(CustomUser customUser) {
        return findUserByEmail(customUser.getEmail()) != null;
    }
}
