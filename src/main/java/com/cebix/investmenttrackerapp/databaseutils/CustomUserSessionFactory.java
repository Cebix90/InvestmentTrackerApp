package com.cebix.investmenttrackerapp.databaseutils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class CustomUserSessionFactory {
    public static SessionFactory getUserSessionFactory() {
        Configuration config = new Configuration();
        config.configure("static/hibernate.cfg.xml");
        return config.buildSessionFactory();
    }
}
