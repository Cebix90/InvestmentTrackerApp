package com.cebix.investmenttrackerapp.handlers;

import com.cebix.investmenttrackerapp.constants.APIConstants;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class StocksAPIHandler {
    private final static String STOCKS_URL = "https://api.polygon.io/v2/aggs/ticker/";

    private final WebClient webClient;

    public StocksAPIHandler() {
        this.webClient = WebClient.builder().baseUrl(STOCKS_URL).build();
    }

    public Mono<String> getStockData(String stockTicker, String multiplier, String timespan, String from, String to, int limit) {
        if (stockTicker == null || stockTicker.isEmpty()) {
            return Mono.error(new RuntimeException("Ticker cannot be empty"));
        }

        try {
            LocalDate fromDate = LocalDate.parse(from);
            LocalDate toDate = LocalDate.parse(to);

            if (fromDate.isAfter(toDate)) {
                return Mono.error(new RuntimeException("The parameter 'to' cannot be a time that occurs before 'from'"));
            }
        } catch (DateTimeParseException e) {
            return Mono.error(new RuntimeException("Invalid date format"));
        }

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
