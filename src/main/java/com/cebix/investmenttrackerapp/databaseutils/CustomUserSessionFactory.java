package com.cebix.investmenttrackerapp.databaseutils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class CustomUserSessionFactory {
    public static SessionFactory getCustomUserSessionFactory() {
        Configuration config = new Configuration();
        config.configure("static/hibernate.cfg.xml");
        return config.buildSessionFactory();
    }
}
