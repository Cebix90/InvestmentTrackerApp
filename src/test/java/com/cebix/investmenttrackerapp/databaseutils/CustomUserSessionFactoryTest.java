package com.cebix.investmenttrackerapp.databaseutils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class CustomUserSessionFactoryTest {
    public static SessionFactory getCustomUserSessionFactory(int port) {
        Configuration config = new Configuration();
        config.configure("static/hibernate.cfg.xml");
        config.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:" + port + "/test_investment_tracker?loggerLevel=OFF");
        return config.buildSessionFactory();
    }
}
