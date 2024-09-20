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

    private CustomUserDAO customUserDAO;
    private PortfolioDAO portfolioDAO;
    private CustomUser userForTests;

    @BeforeEach
    public void testInitialization() {
        int mappedPort = postgresqlContainer.getMappedPort(5432);
        customUserDAO = new CustomUserDAO(CustomUserSessionFactoryTest.getCustomUserSessionFactory(mappedPort));
        portfolioDAO = new PortfolioDAO(CustomUserSessionFactoryTest.getCustomUserSessionFactory(mappedPort));
        userForTests = createAndSaveUserForTests();
    }

    @Nested
    class SavePortfolio {
        @Test
        public void testSavePortfolio_whenPortfolioNotExists_thenCorrect() {
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
            Portfolio newPortfolio = createPortfolioForTests(userForTests);

            Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userForTests.getId());

            assertNotNull(foundPortfolio);
            assertEquals(newPortfolio, foundPortfolio);
        }
    }

    @Nested
    class DeletePortfolio {
        @Test
        public void testDeletePortfolio_whenPortfolioExists_thenCorrect() {
            Portfolio newPortfolio = createPortfolioForTests(userForTests);

            portfolioDAO.deletePortfolio(newPortfolio.getUser().getId());

            Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userForTests.getId());

            assertNull(foundPortfolio);
        }

        @Test
        public void testDeletePortfolio_whenUserHasNoPortfolio_thenThrowsException() {
            Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userForTests.getId());

            assertNull(foundPortfolio);
            assertThrows(UserNotFoundException.class, () -> portfolioDAO.deletePortfolio(userForTests.getId()));
        }
    }

    @Nested
    class UpdatePortfolio {

        @Nested
        class UpdatePortfolioStocksCollection {
            @Test
            public void testUpdatePortfolioStocksCollection_whenPortfolioExists_thenCorrect() {
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
                Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userForTests.getId());

                Map<String, Integer> newStocks = new HashMap<>();
                newStocks.put("AAPL", 10);
                newStocks.put("GOOGL", 5);

                assertNull(foundPortfolio);
                assertThrows(UserNotFoundException.class, () -> portfolioDAO.updatePortfolioStocksCollection(userForTests.getId(), newStocks));
            }

            @Test
            public void testUpdatePortfolioStocksCollection_whenNewStocksCollectionIsNull_thenThrowsException() {
                createPortfolioForTests(userForTests);

                assertThrows(IllegalArgumentException.class, () -> portfolioDAO.updatePortfolioStocksCollection(userForTests.getId(), null));
            }

            @Test
            public void testUpdatePortfolioStocksCollection_whenNewStocksCollectionIsEmpty_thenThrowsException() {
                createPortfolioForTests(userForTests);
                Map<String, Integer> newStocks = new HashMap<>();

                assertThrows(IllegalArgumentException.class, () -> portfolioDAO.updatePortfolioStocksCollection(userForTests.getId(), newStocks));
            }
        }

        @Nested
        class UpdatePortfolioOverallValue {
            @Test
            public void testUpdatePortfolioOverallValue_whenPortfolioExists_thenCorrect() {
                createPortfolioForTests(userForTests);

                Portfolio newPortfolio = portfolioDAO.updatePortfolioOverallValue(userForTests.getId(), 10);
                Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userForTests.getId());

                assertNotNull(foundPortfolio);
                assertEquals(newPortfolio.getOverallValue(), foundPortfolio.getOverallValue());
            }

            @Test
            public void testUpdatePortfolioOverallValue_whenUserHasNoPortfolio_thenThrowsException() {
                Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userForTests.getId());

                assertNull(foundPortfolio);
                assertThrows(UserNotFoundException.class, () -> portfolioDAO.updatePortfolioOverallValue(userForTests.getId(), 10));
            }

            @Test
            public void testUpdatePortfolioOverallValue_whenOverallValueIsLessThanZero_thenThrowsException() {
                createPortfolioForTests(userForTests);

                assertThrows(IllegalArgumentException.class, () -> portfolioDAO.updatePortfolioOverallValue(userForTests.getId(), -1));
            }
        }

        @Nested
        class UpdatePortfolioHistoricalValue {
            @Test
            public void testUpdatePortfolioHistoricalValue_whenPortfolioExists_thenCorrect() {
                createPortfolioForTests(userForTests);

                Map<LocalDate, Double> newHistoricalValues = new HashMap<>();
                newHistoricalValues.put(LocalDate.now().minusDays(1), 10.0);
                newHistoricalValues.put(LocalDate.now().minusDays(2), 5.0);

                Portfolio newPortfolio = portfolioDAO.updatePortfolioHistoricalValue(userForTests.getId(), newHistoricalValues);
                Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userForTests.getId());

                assertNotNull(foundPortfolio);
                assertEquals(newPortfolio.getHistoricalValue(), foundPortfolio.getHistoricalValue());
            }

            @Test
            public void testUpdatePortfolioHistoricalValue_whenUserHasNoPortfolio_thenThrowsException() {
                Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userForTests.getId());

                Map<LocalDate, Double> newHistoricalValues = new HashMap<>();
                newHistoricalValues.put(LocalDate.now().minusDays(1), 10.0);
                newHistoricalValues.put(LocalDate.now().minusDays(2), 5.0);

                assertNull(foundPortfolio);
                assertThrows(UserNotFoundException.class, () -> portfolioDAO.updatePortfolioHistoricalValue(userForTests.getId(), newHistoricalValues));
            }

            @Test
            public void testUpdatePortfolioHistoricalValue_whenNewHistoricalValuesIsNull_thenThrowsException() {
                createPortfolioForTests(userForTests);

                assertThrows(IllegalArgumentException.class, () -> portfolioDAO.updatePortfolioHistoricalValue(userForTests.getId(), null));
            }

            @Test
            public void testUpdatePortfolioHistoricalValue_whenNewHistoricalValuesIsEmpty_thenThrowsException() {
                createPortfolioForTests(userForTests);
                Map<LocalDate, Double> newHistoricalValues = new HashMap<>();

                assertThrows(IllegalArgumentException.class, () -> portfolioDAO.updatePortfolioHistoricalValue(userForTests.getId(), newHistoricalValues));
            }
        }
    }

    private CustomUser createAndSaveUserForTests() {
        CustomUser newUser = new CustomUser();
        newUser.setEmail("test@cebix.com");
        newUser.setPassword("Password123!");
        newUser.setPortfolio(null);

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
