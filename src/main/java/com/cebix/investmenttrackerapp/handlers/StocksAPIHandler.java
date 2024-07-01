package com.cebix.investmenttrackerapp.handlers;

import com.cebix.investmenttrackerapp.constants.APIConstants;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class StocksAPIHandler {
    private final static String STOCKS_URL = "https://api.polygon.io/v2/aggs/ticker/";

    private final WebClient webClient;

    public StocksAPIHandler() {
        this.webClient = WebClient.builder().baseUrl(STOCKS_URL).build();
    }

    public Mono<String> getStockData(String stockTicker, String multiplier, String timespan, String from, String to, int limit) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(STOCKS_URL + "{ticker}/range/{multiplier}/{timespan}/{from}/{to}")
                .queryParam("adjusted", true)
                .queryParam("sort", "asc")
                .queryParam("limit", limit)
                .queryParam("apiKey", APIConstants.API_KEY);

        URI uri = builder.buildAndExpand(stockTicker, multiplier, timespan, from, to).toUri();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                        .flatMap(errorResponse -> Mono.error(new RuntimeException("Error occurred with status code: " + response.statusCode() + " and message: " + errorResponse))))
                .bodyToMono(String.class);
    }
}
