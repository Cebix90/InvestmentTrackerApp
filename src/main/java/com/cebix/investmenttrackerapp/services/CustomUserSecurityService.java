package com.cebix.investmenttrackerapp.services;

import com.cebix.investmenttrackerapp.databaseutils.CustomUserDAO;
import com.cebix.investmenttrackerapp.databaseutils.CustomUserSessionFactory;
import com.cebix.investmenttrackerapp.datamodel.CustomUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserSecurityService implements UserDetailsService {
    private final CustomUserDAO customUserDAO = new CustomUserDAO(CustomUserSessionFactory.getCustomUserSessionFactory());

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUser customUser = customUserDAO.findUserByEmail(username);

        return User.withUsername(customUser.getEmail())
                .password(customUser.getPassword())
                .authorities("USER").build();
    }
}
