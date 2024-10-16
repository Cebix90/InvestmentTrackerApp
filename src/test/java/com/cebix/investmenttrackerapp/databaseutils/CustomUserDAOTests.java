package com.cebix.investmenttrackerapp.databaseutils;

import com.cebix.investmenttrackerapp.datamodel.CustomUser;
import com.cebix.investmenttrackerapp.datamodel.Portfolio;
import com.cebix.investmenttrackerapp.exceptions.UserAlreadyExistsException;
import com.cebix.investmenttrackerapp.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class CustomUserDAOTests {

    DockerImageName postgres = DockerImageName.parse("postgres:16");

    @Container
    PostgreSQLContainer postgresqlContainer = (PostgreSQLContainer) new PostgreSQLContainer(postgres)
            .withDatabaseName("test_investment_tracker")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    private CustomUserDAO customUserDAO;
    private PortfolioDAO portfolioDAO;

    @BeforeEach
    public void testInitialization() {
        int mappedPort = postgresqlContainer.getMappedPort(5432);
        customUserDAO = new CustomUserDAO(CustomUserSessionFactoryTest.getCustomUserSessionFactory(mappedPort));
        portfolioDAO = new PortfolioDAO(CustomUserSessionFactoryTest.getCustomUserSessionFactory(mappedPort));
        System.out.println(mappedPort);
    }

    @Nested
    class SaveUser {
        @Test
        public void testSaveUser_whenUserNotExists_thenCorrect() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);
            CustomUser foundUser = customUserDAO.findUserByEmail("test@example.com");

            assertNotNull(foundUser);
            assertEquals(newUser.getEmail(), foundUser.getEmail());
            assertEquals(newUser.getPassword(), foundUser.getPassword());
            assertEquals(newUser.getPortfolio(), foundUser.getPortfolio());
        }

        @Test
        public void testSaveUser_whenUserExists_thenThrowsException() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);
            CustomUser userForSecondCheck = createUserForTests("test@example.com");

            assertThrows(UserAlreadyExistsException.class, () -> customUserDAO.saveUser(userForSecondCheck));
        }

        @Test
        public void testSaveUser_whenUserHasIncorrectEmail_thenThrowsException() {
            CustomUser newUser = createUserForTests("testexample.com");

            assertThrows(IllegalArgumentException.class, () -> customUserDAO.saveUser(newUser));
        }

        @ParameterizedTest
        @ValueSource(strings = {"pass12", "PASSWORD", "password", "12345678", "pass word", ""})
        public void testSaveUser_whenUserHasIncorrectPassword_thenThrowsException(String invalidPassword) {
            CustomUser newUser = new CustomUser();
            newUser.setEmail("test@example.com");
            newUser.setPassword(invalidPassword);
            newUser.setPortfolio(null);

            assertThrows(IllegalArgumentException.class, () -> customUserDAO.saveUser(newUser));
        }
    }

    @Nested
    class FindUserByEmail {
        @Test
        public void testFindUserByEmail_whenUserFound_thenCorrect() {
            CustomUser newUser = createUserForTests("testfind@example.com");

            customUserDAO.saveUser(newUser);
            CustomUser foundUser = customUserDAO.findUserByEmail("testfind@example.com");

            assertNotNull(foundUser);
            assertEquals("testfind@example.com", foundUser.getEmail());
        }

        @Test
        public void testFindUserByEmail_whenEmailIsEmpty_thenThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> customUserDAO.findUserByEmail(""));
        }

        @Test
        public void testFindUserByEmail_whenEmailIsNull_thenThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> customUserDAO.findUserByEmail(null));
        }
    }

    @Nested
    class DeleteUser {
        @Test
        public void testDeleteUser_whenUserExists_thenCorrect() {
            CustomUser newUser = createUserForTests("testdelete@example.com");

            customUserDAO.saveUser(newUser);
            customUserDAO.deleteUser("testdelete@example.com");
            CustomUser foundUser = customUserDAO.findUserByEmail("testdelete@example.com");

            assertNull(foundUser);
        }

        @Test
        public void testDeleteUser__whenUserNotFound_thenThrowsException() {
            assertThrows(UserNotFoundException.class, () -> customUserDAO.deleteUser("testdeleteincorrect@example.com"));
        }
    }

    @Nested
    class UpdateUser {
        @Test
        public void testUpdateUserEmail_whenUserExists_thenCorrect() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);
            newUser = customUserDAO.updateUserEmail("test@example.com", "updatedtest@example.com");
            CustomUser foundUser = customUserDAO.findUserByEmail("updatedtest@example.com");

            assertNotNull(foundUser);
            assertEquals(newUser.getEmail(), foundUser.getEmail());
            assertEquals(newUser.getPassword(), foundUser.getPassword());
            assertEquals(newUser.getPortfolio(), foundUser.getPortfolio());
        }

        @Test
        public void testUpdateUserEmail_whenUserNotFound_thenThrowsException() {
            assertThrows(UserNotFoundException.class, () -> customUserDAO.updateUserEmail("inccorect@example.com", "updatedtest@example.com"));
        }

        @Test
        public void testUpdateUserEmail_whenNewEmailIsNull_thenThrowsException() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);

            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserEmail("test@example.com", null));
        }

        @Test
        public void testUpdateUserEmail_whenNewEmailIsEmpty_thenThrowsException() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);

            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserEmail("test@example.com", ""));
        }

        @Test
        public void testUpdateUserEmail_whenNewEmailIsNotCorrectFormat_thenThrowsException() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);

            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserEmail("test@example.com", "username.@domain.com"));
            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserEmail("test@example.com", ".user.name@domain.com"));
            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserEmail("test@example.com", "user-name@domain.com."));
            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserEmail("test@example.com", "username@.com"));
            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserEmail("test@example.com", "username.com"));
        }

        @Test
        public void testUpdateUserPassword_whenUserExists_thenCorrect() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);
            newUser = customUserDAO.updateUserPassword("test@example.com", "updPass123!");
            CustomUser foundUser = customUserDAO.findUserByEmail("test@example.com");

            assertNotNull(foundUser);
            assertEquals(newUser.getEmail(), foundUser.getEmail());
            assertEquals(newUser.getPassword(), foundUser.getPassword());
            assertEquals(newUser.getPortfolio(), foundUser.getPortfolio());
        }

        @Test
        public void testUpdateUserPassword_whenUserNotFound_thenThrowsException() {
            assertThrows(UserNotFoundException.class, () -> customUserDAO.updateUserPassword("inccorect@example.com", "updatedPassword123"));
        }

        @Test
        public void testUpdateUserPassword_whenNewPasswordIsNull_thenThrowsException() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);

            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserPassword("test@example.com", null));
        }

        @Test
        public void testUpdateUserPassword_whenNewPasswordIsEmpty_thenThrowsException() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);

            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserPassword("test@example.com", ""));
        }

        @Test
        public void testUpdateUserPassword_whenNewPasswordIsNotCorrectFormat_thenThrowsException() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);

            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserPassword("test@example.com", "PASSWORD123!"));
            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserPassword("test@example.com", "password123!"));
            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserPassword("test@example.com", "Password123"));
            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserPassword("test@example.com", "Password!"));
        }

        @Test
        public void testUpdateUserPortfolio_whenUserExists_thenCorrect() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);

            Map<String, Integer> initialStocks = new HashMap<>();
            initialStocks.put("AAPL", 10);
            initialStocks.put("TSLA", 5);
            Portfolio portfolio = new Portfolio(newUser, initialStocks, 1000.0, new HashMap<>());
            portfolioDAO.savePortfolio(portfolio);

            newUser = customUserDAO.updateUserPortfolio("test@example.com", portfolio);

            CustomUser foundUser = customUserDAO.findUserByEmail("test@example.com");

            assertNotNull(foundUser);
            assertEquals(newUser.getEmail(), foundUser.getEmail());
            assertEquals(newUser.getPassword(), foundUser.getPassword());
            assertEquals(newUser.getPortfolio().getId(), foundUser.getPortfolio().getId());
        }

        @Test
        public void testUpdateUserPortfolio_whenUserNotFound_thenThrowsException() {
            assertThrows(UserNotFoundException.class, () -> customUserDAO.updateUserPortfolio("inccorect@example.com", new Portfolio()));
        }

        @Test
        public void testUpdateUserPortfolio_whenNewPortfolioIsNull_thenThrowsException() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);

            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserPortfolio("test@example.com", null));
        }
    }

    private CustomUser createUserForTests(String email) {
        CustomUser newUser = new CustomUser();
        newUser.setEmail(email);
        newUser.setPassword("Password123!");
        newUser.setPortfolio(null);

        return newUser;
    }
}
