package com.cebix.investmenttrackerapp.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.LocalDate;

@SpringBootTest
public class TechnicalIndicatorsAPIHandlerTests {
    @Autowired
    private TechnicalIndicatorsAPIHandler technicalIndicatorsAPIHandler;

    @Nested
    class TestSMADData {
        @Test
        public void testGetSMADData_ifSuccess() {
            String stockTicker = "AAPL";
            String timestamp = "2023-09-01";
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getSMAData(stockTicker, timestamp, timespan))
                    .expectNextMatches(response -> {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode responseJson = objectMapper.readTree(response);
                            JsonNode resultsNode = responseJson.get("results");
                            JsonNode valuesNode = resultsNode != null ? resultsNode.get("values") : null;
                            return response != null && !response.isEmpty() && valuesNode != null;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .verifyComplete();
        }

        @Test
        public void testGetSMAData_throwsError_whenTickerIsEmpty() {
            String stockTicker = "";
            String timestamp = "2023-09-01";
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getSMAData(stockTicker, timestamp, timespan))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("Ticker cannot be empty"))
                    .verify();
        }

        @Test
        public void testGetSMAData_throwsError_whenTickerIsNull() {
            String stockTicker = null;
            String timestamp = "2023-09-01";
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getSMAData(stockTicker, timestamp, timespan))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("Ticker cannot be empty"))
                    .verify();
        }

        @Test
        public void testGetSMAData_throwsError403_whenDateIsGraterThanToday() {
            String stockTicker = "AAPL";
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            String timestamp = tomorrow.toString();
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getSMAData(stockTicker, timestamp, timespan))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("The timestamp cannot be a time that occurs today date"))
                    .verify();
        }
    }

    @Nested
    class TestEMAData {
        @Test
        public void testGetEMAData_ifSuccess() {
            String stockTicker = "AAPL";
            String timestamp = "2023-09-01";
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getEMAData(stockTicker, timestamp, timespan))
                    .expectNextMatches(response -> {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode responseJson = objectMapper.readTree(response);
                            JsonNode resultsNode = responseJson.get("results");
                            JsonNode valuesNode = resultsNode != null ? resultsNode.get("values") : null;
                            return response != null && !response.isEmpty() && valuesNode != null;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .verifyComplete();
        }

        @Test
        public void testGetEMAData_throwsError_whenTickerIsEmpty() {
            String stockTicker = "";
            String timestamp = "2023-09-01";
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getEMAData(stockTicker, timestamp, timespan))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("Ticker cannot be empty"))
                    .verify();
        }

        @Test
        public void testGetEMAData_throwsError_whenTickerIsNull() {
            String stockTicker = null;
            String timestamp = "2023-09-01";
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getEMAData(stockTicker, timestamp, timespan))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("Ticker cannot be empty"))
                    .verify();
        }

        @Test
        public void testGetEMAData_throwsError403_whenDateIsGraterThanToday() {
            String stockTicker = "AAPL";
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            String timestamp = tomorrow.toString();
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getEMAData(stockTicker, timestamp, timespan))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("The timestamp cannot be a time that occurs today date"))
                    .verify();
        }
    }

    @Nested
    class TestMACDData {
        @Test
        public void testGetMACDData_ifSuccess() {
            String stockTicker = "AAPL";
            String timestamp = "2023-09-01";
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getMACDData(stockTicker, timestamp, timespan))
                    .expectNextMatches(response -> {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode responseJson = objectMapper.readTree(response);
                            JsonNode resultsNode = responseJson.get("results");
                            JsonNode valuesNode = resultsNode != null ? resultsNode.get("values") : null;
                            return response != null && !response.isEmpty() && valuesNode != null;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .verifyComplete();
        }

        @Test
        public void testGetMACDData_throwsError_whenTickerIsEmpty() {
            String stockTicker = "";
            String timestamp = "2023-09-01";
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getMACDData(stockTicker, timestamp, timespan))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("Ticker cannot be empty"))
                    .verify();
        }

        @Test
        public void testGetMACDData_throwsError_whenTickerIsNull() {
            String stockTicker = null;
            String timestamp = "2023-09-01";
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getMACDData(stockTicker, timestamp, timespan))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("Ticker cannot be empty"))
                    .verify();
        }

        @Test
        public void testGetMACDData_throwsError403_whenDateIsGraterThanToday() {
            String stockTicker = "AAPL";
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            String timestamp = tomorrow.toString();
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getMACDData(stockTicker, timestamp, timespan))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("The timestamp cannot be a time that occurs today date"))
                    .verify();
        }
    }

    @Nested
    class TestRSIData {
        @Test
        public void testGetRSIData_ifSuccess() {
            String stockTicker = "AAPL";
            String timestamp = "2023-09-01";
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getRSIData(stockTicker, timestamp, timespan))
                    .expectNextMatches(response -> {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            JsonNode responseJson = objectMapper.readTree(response);
                            JsonNode resultsNode = responseJson.get("results");
                            JsonNode valuesNode = resultsNode != null ? resultsNode.get("values") : null;
                            return response != null && !response.isEmpty() && valuesNode != null;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .verifyComplete();
        }

        @Test
        public void testGetRSIData_throwsError_whenTickerIsEmpty() {
            String stockTicker = "";
            String timestamp = "2023-09-01";
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getRSIData(stockTicker, timestamp, timespan))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("Ticker cannot be empty"))
                    .verify();
        }

        @Test
        public void testGetRSIData_throwsError_whenTickerIsNull() {
            String stockTicker = null;
            String timestamp = "2023-09-01";
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getRSIData(stockTicker, timestamp, timespan))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("Ticker cannot be empty"))
                    .verify();
        }

        @Test
        public void testGetRSIData_throwsError403_whenDateIsGraterThanToday() {
            String stockTicker = "AAPL";
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            String timestamp = tomorrow.toString();
            String timespan = "day";

            StepVerifier.create(technicalIndicatorsAPIHandler.getRSIData(stockTicker, timestamp, timespan))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().contains("The timestamp cannot be a time that occurs today date"))
                    .verify();
        }
    }
}
