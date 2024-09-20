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
import java.util.regex.Pattern;

public class CustomUserDAO {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SessionFactory sessionFactory;

    public CustomUserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveUser(CustomUser customUser) {
        if (!validateEmail(customUser.getEmail())) {
            throw new IllegalArgumentException(getValidationErrorMessage("Email", customUser.getEmail()));
        }

        if (!validatePassword(customUser.getPassword())) {
            throw new IllegalArgumentException(getValidationErrorMessage("Password", customUser.getPassword()));
        }

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
        final String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-Za-z0-9\\+_-]+)*@"
                + "[^-][A-Za-z0-9\\+-]+(\\.[A-Za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$";

        return email != null && Pattern.compile(regexPattern).matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        final String regexPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!?@#$%^&+=]).{8,}$";

        return password != null && Pattern.compile(regexPattern).matcher(password).matches();
    }

    private boolean validatePortfolio(Portfolio portfolio) {
        return portfolio != null;
    }

    private <T> String getValidationErrorMessage(String fieldName, T value) {
        if (value instanceof String && ((String) value).trim().isEmpty()) {
            return fieldName + " cannot empty.";
        }

        if (fieldName.equalsIgnoreCase("Password")) {
            return getPasswordValidationMessage();
        } else if (fieldName.equalsIgnoreCase("Email")) {
            return getEmailValidationMessage();
        }

        return fieldName + " cannot be null.";
    }

    private String getPasswordValidationMessage() {
        return "Password must contains at least: 8 characters, one uppercase and lowercase letter, one digit, one special character, no spaces or tabs";
    }

    private String getEmailValidationMessage() {
        return "Email format should be in the form: username@domain.com";
    }

    private boolean userExists(CustomUser customUser) {
        return findUserByEmail(customUser.getEmail()) != null;
    }
}
