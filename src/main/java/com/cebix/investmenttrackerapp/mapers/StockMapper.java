package com.cebix.investmenttrackerapp.mapers;

import com.cebix.investmenttrackerapp.datamodel.Stock;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class StockMapper {
    public static Stock mapJSONToStock(String stockJSON) {
        JSONObject object;
        JSONTokener tokener = new JSONTokener(stockJSON);

        try {
            object = new JSONObject(tokener);
        } catch (JSONException e) {
            tokener = new JSONTokener(stockJSON);
            object = new JSONArray(tokener).getJSONObject(0);
        }

        String ticker = object.getString("ticker");

        double value = 0.0;
        long timestamp = 0;
        JSONArray resultsArray = object.getJSONArray("results");
        if (resultsArray != null && !resultsArray.isEmpty()) {
            JSONObject resultObject = resultsArray.getJSONObject(0);
            value = resultObject.getDouble("c");
            timestamp = resultObject.getLong("t");
        }

        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();

        return new Stock(ticker, value, date);
    }
}