package com.cebix.investmenttrackerapp.databaseutils;

import com.cebix.investmenttrackerapp.datamodel.CustomUser;
import com.cebix.investmenttrackerapp.datamodel.Portfolio;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

//    CustomUser userForTests;

    @Nested
    class SavePortfolio {
        @Test
        public void testSavePortfolio_whenPortfolioNotExists_thenCorrect() {
            CustomUser userForTests = createUserForTests();
            customUserDAO.saveUser(userForTests);

            Portfolio newPortfolio = createPortfolioForTests(userForTests);
            portfolioDAO.savePortfolio(newPortfolio);

            Portfolio foundPortfolio = portfolioDAO.findPortfolioByUserId(userForTests.getId());

            assertNotNull(foundPortfolio);
            assertEquals(newPortfolio.getUser(), foundPortfolio.getUser());
            assertEquals(newPortfolio.getStocks(), foundPortfolio.getStocks());
            assertEquals(newPortfolio.getOverallValue(), foundPortfolio.getOverallValue());
            assertEquals(newPortfolio.getHistoricalValue(), foundPortfolio.getHistoricalValue());
        }
    }

    private CustomUser createUserForTests() {
        CustomUser newUser = new CustomUser();
        newUser.setEmail("test@cebix.com");
        newUser.setPassword("password123");
        newUser.setPortfolio("Test Portfolio");

        return newUser;
    }

    private Portfolio createPortfolioForTests(CustomUser userForTests) {
        Map<String, Integer> stocks = new HashMap<>();
        stocks.put("Test", 1);

        Map<LocalDate, Double> historicalValue = new HashMap<>();
        historicalValue.put(LocalDate.now(), 1.0);

        return new Portfolio(userForTests, stocks, 1.0, historicalValue);
    }
}
