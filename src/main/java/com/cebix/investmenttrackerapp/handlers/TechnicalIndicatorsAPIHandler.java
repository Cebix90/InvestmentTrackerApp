package com.cebix.investmenttrackerapp.handlers;

import com.cebix.investmenttrackerapp.constants.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class TechnicalIndicatorsAPIHandler {
    private final String TECHNICAL_INDICATORS_URL = "https://api.polygon.io/v1/indicators/";

    private final WebClient webClient;

    @Autowired
    public TechnicalIndicatorsAPIHandler(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(TECHNICAL_INDICATORS_URL).build();
    }

    private Mono<String> getTechnicalIndicatorData(String stockTicker, String timestamp, String timespan, String indicator, Map<String, Object> additionalParams) {
        if (stockTicker == null || stockTicker.isEmpty()) {
            return Mono.error(new RuntimeException("Ticker cannot be empty"));
        }

        LocalDate futureTimestamp = LocalDate.parse(timestamp);

        if (futureTimestamp.isAfter(LocalDate.now())) {
            return Mono.error(new RuntimeException("The timestamp cannot be a time that occurs today date"));
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(TECHNICAL_INDICATORS_URL + indicator + "/{ticker}")
                .queryParam("timestamp", timestamp)
                .queryParam("timespan", timespan)
                .queryParam("adjusted", true)
                .queryParam("series_type", "close")
                .queryParam("order", "desc")
                .queryParam("apiKey", APIConstants.API_KEY);

        for (Map.Entry<String, Object> entry : additionalParams.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        URI uri = builder.buildAndExpand(stockTicker).toUri();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                        .flatMap(errorResponse -> Mono.error(new RuntimeException("Error occurred with status code: " + response.statusCode() + " and message: " + errorResponse))))
                .bodyToMono(String.class);
    }

    public Mono<String> getSMAData(String stockTicker, String timestamp, String timespan) {
        Map<String, Object> params = new HashMap<>();
        params.put("window", 50);
        return getTechnicalIndicatorData(stockTicker, timestamp, timespan, "sma", params);
    }

    public Mono<String> getEMAData(String stockTicker, String timestamp, String timespan) {
        Map<String, Object> params = new HashMap<>();
        params.put("window", 50);
        return getTechnicalIndicatorData(stockTicker, timestamp, timespan, "ema", params);
    }

    public Mono<String> getMACDData(String stockTicker, String timestamp, String timespan) {
        Map<String, Object> params = new HashMap<>();
        params.put("short_window", 12);
        params.put("long_window", 26);
        params.put("signal_window", 9);
        return getTechnicalIndicatorData(stockTicker, timestamp, timespan, "macd", params);
    }

    public Mono<String> getRSIData(String stockTicker, String timestamp, String timespan) {
        Map<String, Object> params = new HashMap<>();
        params.put("window", 14);
        return getTechnicalIndicatorData(stockTicker, timestamp, timespan, "rsi", params);
    }
}
