package com.cebix.investmenttrackerapp.handlers;

import com.cebix.investmenttrackerapp.constants.APIConstants;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class MarketStatusAPIHandler {
    private final static String MARKET_STATUS_URL = "https://api.polygon.io/v1/marketstatus/now";

    private final WebClient webClient;

    public MarketStatusAPIHandler() {
        this.webClient = WebClient.builder().baseUrl(MARKET_STATUS_URL).build();
    }

    public Mono<String> getMarketStatusData() {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(MARKET_STATUS_URL)
                .queryParam("apiKey", APIConstants.API_KEY);

        URI uri = builder.buildAndExpand().toUri();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                        .flatMap(errorResponse -> Mono.error(new RuntimeException("Error occurred with status code: " + response.statusCode() + " and message: " + errorResponse))))
                .bodyToMono(String.class);
    }
}
