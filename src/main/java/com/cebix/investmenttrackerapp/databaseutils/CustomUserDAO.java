package com.cebix.investmenttrackerapp.databaseutils;

import com.cebix.investmenttrackerapp.datamodel.CustomUser;
import com.cebix.investmenttrackerapp.exceptions.UserExistsException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class CustomUserDAO {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SessionFactory sessionFactory = CustomUserSessionFactory.getCustomUserSessionFactory();

    public void saveUser(CustomUser customUser) {
        Session session = null;
        Transaction transaction = null;
        try {
            if(!userExists(customUser)) {
                session = sessionFactory.openSession();
                transaction = session.beginTransaction();
                customUser.setPassword(passwordEncoder.encode(customUser.getPassword()));
                session.merge(customUser);
                transaction.commit();
            } else {
                throw new UserExistsException();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public CustomUser findUserByEmail(String email) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<CustomUser> userQuery = criteriaBuilder.createQuery(CustomUser.class);
            Root<CustomUser> userRoot = userQuery.from(CustomUser.class);
            userQuery.select(userRoot).where(criteriaBuilder.equal(userRoot.get("email"), email));
            CustomUser customUser = session.createQuery(userQuery).getSingleResultOrNull();
            return customUser;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void deleteUser(String customUserEmail) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            CustomUser customUserToDelete = findUserByEmail(customUserEmail);
            if (customUserToDelete != null) {
                session.remove(customUserToDelete);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void updateUser(CustomUser customUser) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            CustomUser customUserToUpdate = findUserByEmail(customUser.getEmail());
            if (customUserToUpdate != null) {
                customUserToUpdate.setPassword(passwordEncoder.encode(customUser.getPassword()));
                customUserToUpdate.setPortfolio(customUser.getPortfolio());
                session.merge(customUserToUpdate);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private boolean userExists(CustomUser customUser) {
        return findUserByEmail(customUser.getEmail()) != null;
    }
}
