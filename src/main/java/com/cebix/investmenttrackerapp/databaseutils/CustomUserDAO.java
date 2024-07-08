package com.cebix.investmenttrackerapp.databaseutils;

import com.cebix.investmenttrackerapp.datamodel.CustomUser;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CustomUserDAO {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SessionFactory sessionFactory = CustomUserSessionFactory.getCustomUserSessionFactory();

    public void saveUser(CustomUser customUser) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        customUser.setPassword(passwordEncoder.encode(customUser.getPassword()));
        session.merge(customUser);
        transaction.commit();
        session.close();
    }

    public CustomUser findUserByEmail(String email) {
        Session session = sessionFactory.openSession();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<CustomUser> userQuery = criteriaBuilder.createQuery(CustomUser.class);
        Root<CustomUser> userRoot = userQuery.from(CustomUser.class);
        userQuery.select(userRoot).where(criteriaBuilder.equal(userRoot.get("email"), email));
        CustomUser customUser = session.createQuery(userQuery).getSingleResultOrNull();
        session.close();
        return customUser;
    }

    public void deleteUser(String customUserEmail) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        CustomUser customUserToDelete = findUserByEmail(customUserEmail);
        if (customUserToDelete != null) {
            session.remove(customUserToDelete);
            transaction.commit();
        }
        session.close();
    }

    public void updateUser(CustomUser customUser) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        CustomUser customUserToUpdate = findUserByEmail(customUser.getEmail());
        if (customUserToUpdate != null) {
            customUserToUpdate.setPassword(passwordEncoder.encode(customUser.getPassword()));
            customUserToUpdate.setPortfolio(customUser.getPortfolio());
            session.merge(customUserToUpdate);
            transaction.commit();
        }
        session.close();
    }
}
