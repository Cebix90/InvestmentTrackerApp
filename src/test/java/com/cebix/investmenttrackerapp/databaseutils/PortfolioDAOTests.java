package com.cebix.investmenttrackerapp.databaseutils;

import com.cebix.investmenttrackerapp.datamodel.CustomUser;
import com.cebix.investmenttrackerapp.datamodel.Portfolio;
import com.cebix.investmenttrackerapp.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class PortfolioDAOTests {
    DockerImageName postgres = DockerImageName.parse("postgres:16");

    @Container
    PostgreSQLContainer postgresqlContainer = (PostgreSQLContainer) new PostgreSQLContainer(postgres)
            .withDatabaseName("test_investment_tracker")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    CustomUserDAO customUserDAO;
    PortfolioDAO portfolioDAO;

    @BeforeEach
    public void testInitialization() {
        int mappedPort = postgresqlContainer.getMappedPort(5432);
        customUserDAO = new CustomUserDAO(CustomUserSessionFactoryTest.getCustomUserSessionFactory(mappedPort));
        portfolioDAO = new PortfolioDAO(CustomUserSessionFactoryTest.getCustomUserSessionFactory(mappedPort));
        System.out.println(mappedPort);
    }

    @Nested
    class SavePortfolio {
        @Test
        public void testSavePortfolio_whenPortfolioNotExists_thenCorrect() {
            CustomUser userForTests = createAndSaveUserForTests();

            Portfolio newPortfolio = createPortfolioForTests(userForTests);

            Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userForTests.getId());

            assertNotNull(foundPortfolio);
            assertEquals(newPortfolio.getUser(), foundPortfolio.getUser());
            assertEquals(newPortfolio.getStocks(), foundPortfolio.getStocks());
            assertEquals(newPortfolio.getOverallValue(), foundPortfolio.getOverallValue());
            assertEquals(newPortfolio.getHistoricalValue(), foundPortfolio.getHistoricalValue());
        }

        @Test
        public void testSavePortfolio_whenUserNotExistsForPortfolio_thenThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> portfolioDAO.savePortfolio(createPortfolioForTests(null)));
        }
    }

    @Nested
    class findPortfolioByUserId {
        @Test
        public void testFindPortfolioByUserId_whenUserFound_thenCorrect() {
            CustomUser userForTests = createAndSaveUserForTests();

            Portfolio newPortfolio = createPortfolioForTests(userForTests);

            Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userForTests.getId());

            assertNotNull(foundPortfolio);
            assertEquals(newPortfolio, foundPortfolio);
        }

        @Test
        public void testFindPortfolioByUserId_whenUserIdIsNull_thenThrowsException() {
            assertThrows(UserNotFoundException.class, () -> portfolioDAO.findPortfolioByUserId(null));
        }
    }

    @Nested
    class DeletePortfolio {
        @Test
        public void testDeletePortfolio_whenPortfolioExists_thenCorrect() {
            CustomUser userForTests = createAndSaveUserForTests();
            Portfolio newPortfolio = createPortfolioForTests(userForTests);

            portfolioDAO.deletePortfolio(newPortfolio.getUser().getId());

            Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userForTests.getId());

            assertNull(foundPortfolio);
        }

        @Test
        public void testDeletePortfolio__whenUserIdIsNull_thenThrowsException() {
            assertThrows(UserNotFoundException.class, () -> portfolioDAO.deletePortfolio(null));
        }

        @Test
        public void testDeletePortfolio_whenUserHasNoPortfolio_thenThrowsException() {
            CustomUser userWithoutPortfolio = createAndSaveUserForTests();
            Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userWithoutPortfolio.getId());

            assertNull(foundPortfolio);
            assertThrows(UserNotFoundException.class, () -> portfolioDAO.deletePortfolio(userWithoutPortfolio.getId()));
        }
    }

    @Nested
    class UpdatePortfolio {
        @Test
        public void testUpdatePortfolioStocksCollection_whenPortfolioExists_thenCorrect() {
            CustomUser userForTests = createAndSaveUserForTests();
            createPortfolioForTests(userForTests);

            Map<String, Integer> newStocks = new HashMap<>();
            newStocks.put("AAPL", 10);
            newStocks.put("GOOGL", 5);

            Portfolio newPortfolio = portfolioDAO.updatePortfolioStocksCollection(userForTests.getId(), newStocks);
            Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userForTests.getId());

            assertNotNull(foundPortfolio);
            assertEquals(newPortfolio.getStocks(), foundPortfolio.getStocks());
        }

        @Test
        public void testUpdatePortfolioStocksCollection_whenUserHasNoPortfolio_thenThrowsException() {
            CustomUser userWithoutPortfolio = createAndSaveUserForTests();
            Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userWithoutPortfolio.getId());

            Map<String, Integer> newStocks = new HashMap<>();
            newStocks.put("AAPL", 10);
            newStocks.put("GOOGL", 5);

            assertNull(foundPortfolio);
            assertThrows(UserNotFoundException.class, () -> portfolioDAO.updatePortfolioStocksCollection(userWithoutPortfolio.getId(), newStocks));
        }

        @Test
        public void testUpdatePortfolioStocksCollection_whenNewStocksCollectionIsNull_thenThrowsException() {
            CustomUser userForTests = createAndSaveUserForTests();
            createPortfolioForTests(userForTests);

            assertThrows(IllegalArgumentException.class, () -> portfolioDAO.updatePortfolioStocksCollection(userForTests.getId(), null));
        }

        @Test
        public void testUpdatePortfolioStocksCollection_whenNewStocksCollectionIsEmpty_thenThrowsException() {
            CustomUser userForTests = createAndSaveUserForTests();
            createPortfolioForTests(userForTests);
            Map<String, Integer> newStocks = new HashMap<>();

            assertThrows(IllegalArgumentException.class, () -> portfolioDAO.updatePortfolioStocksCollection(userForTests.getId(), newStocks));
        }
    }

    private CustomUser createAndSaveUserForTests() {
        CustomUser newUser = new CustomUser();
        newUser.setEmail("test@cebix.com");
        newUser.setPassword("password123");
        newUser.setPortfolio("Test Portfolio");

        customUserDAO.saveUser(newUser);

        return newUser;
    }

    private Portfolio createPortfolioForTests(CustomUser userForTests) {
        Map<String, Integer> stocks = new HashMap<>();
        stocks.put("Test", 1);

        Map<LocalDate, Double> historicalValue = new HashMap<>();
        historicalValue.put(LocalDate.now(), 1.0);

        Portfolio newPortfolio = new Portfolio(userForTests, stocks, 1.0, historicalValue);

        portfolioDAO.savePortfolio(newPortfolio);

        return newPortfolio;
    }
}
