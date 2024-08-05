package com.cebix.investmenttrackerapp.databaseutils;

import com.cebix.investmenttrackerapp.datamodel.CustomUser;
import com.cebix.investmenttrackerapp.exceptions.UserAlreadyExistsException;
import com.cebix.investmenttrackerapp.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

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

    CustomUserDAO customUserDAO;

    @BeforeEach
    public void testInitialization() {
        int mappedPort = postgresqlContainer.getMappedPort(5432);
        customUserDAO = new CustomUserDAO(CustomUserSessionFactoryTest.getCustomUserSessionFactory(mappedPort));
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

            assertThrows(UserAlreadyExistsException.class, () -> customUserDAO.saveUser(newUser));
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
    class UpdateUser{
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
        public void testUpdateUserPassword_whenUserExists_thenCorrect() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);
            newUser = customUserDAO.updateUserPassword("test@example.com", "updatedPassword123");
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
        public void testUpdateUserPassword_whenNewEmailIsNull_thenThrowsException() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);

            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserPassword("test@example.com", null));
        }

        @Test
        public void testUpdateUserPassword_whenNewEmailIsEmpty_thenThrowsException() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);

            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserPassword("test@example.com", ""));
        }

        @Test
        public void testUpdateUserPortfolio_whenUserExists_thenCorrect() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);
            newUser = customUserDAO.updateUserPortfolio("test@example.com", "Updated Portfolio");
            CustomUser foundUser = customUserDAO.findUserByEmail("test@example.com");

            assertNotNull(foundUser);
            assertEquals(newUser.getEmail(), foundUser.getEmail());
            assertEquals(newUser.getPassword(), foundUser.getPassword());
            assertEquals(newUser.getPortfolio(), foundUser.getPortfolio());
        }

        @Test
        public void testUpdateUserPortfolio_whenUserNotFound_thenThrowsException() {
            assertThrows(UserNotFoundException.class, () -> customUserDAO.updateUserPortfolio("inccorect@example.com", "Updated Portfolio"));
        }

        @Test
        public void testUpdateUserPortfolio_whenNewEmailIsNull_thenThrowsException() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);

            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserPortfolio("test@example.com", null));
        }

        @Test
        public void testUpdateUserPortfolio_whenNewEmailIsEmpty_thenThrowsException() {
            CustomUser newUser = createUserForTests("test@example.com");
            customUserDAO.saveUser(newUser);

            assertThrows(IllegalArgumentException.class, () -> customUserDAO.updateUserPortfolio("test@example.com", ""));
        }
    }

    private CustomUser createUserForTests(String email) {
        CustomUser newUser = new CustomUser();
        newUser.setEmail(email);
        newUser.setPassword("password123");
        newUser.setPortfolio("Test Portfolio");

        return newUser;
    }
}
