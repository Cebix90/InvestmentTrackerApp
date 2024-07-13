package com.cebix.investmenttrackerapp.databaseutils;

import com.cebix.investmenttrackerapp.datamodel.CustomUser;
import com.cebix.investmenttrackerapp.exceptions.UserExistsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CustomUserDAOTests {
    private final CustomUserDAO customUserDAO = new CustomUserDAO();

    @Test
    public void testSaveUser_whenUserNotExists_thenCorrect() {
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
    public void testSaveUser_whenUserExists_thenThrowsException() {
        CustomUser newUser = new CustomUser();
        newUser.setEmail("test@example.com");
        newUser.setPassword("password123");
        newUser.setPortfolio("Test Portfolio");

        customUserDAO.saveUser(newUser);

        assertThrows(UserExistsException.class, () -> customUserDAO.saveUser(newUser));
    }

    @Test
    public void testFindUserByEmail_whenUserFound() {
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
    public void testFindUserByEmail_whenEmailIsEmpty_thenThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> customUserDAO.findUserByEmail(""));

        assertEquals("Email must not be null or empty", exception.getMessage());
    }

    @Test
    public void testFindUserByEmail_whenEmailIsNull_thenThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> customUserDAO.findUserByEmail(null));

        assertEquals("Email must not be null or empty", exception.getMessage());
    }

    @Test
    public void testUpdateUser_whenUserExists_thenCorrect() {
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
    public void testDeleteUser_whenUserExists_thenCorrect() {
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
