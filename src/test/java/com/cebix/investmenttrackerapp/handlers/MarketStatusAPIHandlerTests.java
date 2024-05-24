package com.cebix.investmenttrackerapp.handlers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
public class MarketStatusAPIHandlerTests {

    @Autowired
    private MarketStatusAPIHandler marketStatusAPIHandler;

    @Test
    public void testGetMarketStatusData_ifCorrectResponse() {
        StepVerifier.create(marketStatusAPIHandler.getMarketStatusData())
                .expectNextMatches(data -> data.contains("\"market\":\"open\"") || data.contains("\"market\":\"closed\""))
                .verifyComplete();
    }
}
