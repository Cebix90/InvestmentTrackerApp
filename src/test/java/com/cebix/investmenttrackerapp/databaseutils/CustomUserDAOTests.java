package com.cebix.investmenttrackerapp.databaseutils;

import com.cebix.investmenttrackerapp.datamodel.CustomUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CustomUserDAOTests {

    @Autowired
    private CustomUserDAO customUserDAO;

    @Test
    public void testSaveUser() {
        CustomUser newUser = new CustomUser();
        newUser.setEmail("test@example.com");
        newUser.setPassword("password123");
        newUser.setPortfolio("Test Portfolio");

        customUserDAO.saveUser(newUser);
        CustomUser foundUser = customUserDAO.findUserByEmail("test@example.com");

        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getEmail());
        assertNotEquals("password123", foundUser.getPassword());
        assertEquals("Test Portfolio", foundUser.getPortfolio());
    }

    @Test
    public void testFindUserByEmail() {
        CustomUser newUser = new CustomUser();
        newUser.setEmail("testfind@example.com");
        newUser.setPassword("password123");
        newUser.setPortfolio("Test Portfolio");

        customUserDAO.saveUser(newUser);
        CustomUser foundUser = customUserDAO.findUserByEmail("testfind@example.com");

        assertNotNull(foundUser);
        assertEquals("testfind@example.com", foundUser.getEmail());
    }

    @Test
    public void testUpdateUser() {
        CustomUser newUser = new CustomUser();
        newUser.setEmail("testupdate@example.com");
        newUser.setPassword("password123");
        newUser.setPortfolio("Test Portfolio");

        customUserDAO.saveUser(newUser);
        CustomUser foundUser = customUserDAO.findUserByEmail("testupdate@example.com");

        foundUser.setPassword("newpassword123");
        foundUser.setPortfolio("Updated Portfolio");
        customUserDAO.updateUser(foundUser);

        CustomUser updatedUser = customUserDAO.findUserByEmail("testupdate@example.com");

        assertNotNull(updatedUser);
        assertNotEquals("password123", updatedUser.getPassword());
        assertEquals("Updated Portfolio", updatedUser.getPortfolio());
    }

    @Test
    public void testDeleteUser() {
        CustomUser newUser = new CustomUser();
        newUser.setEmail("testdelete@example.com");
        newUser.setPassword("password123");
        newUser.setPortfolio("Test Portfolio");

        customUserDAO.saveUser(newUser);
        customUserDAO.deleteUser("testdelete@example.com");
        CustomUser foundUser = customUserDAO.findUserByEmail("testdelete@example.com");

        assertNull(foundUser);
    }
}