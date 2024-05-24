package com.cebix.investmenttrackerapp.mappers;

import com.cebix.investmenttrackerapp.datamodel.TechnicalIndicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.LocalDate;

public class TechnicalIndicatorMapper {
    public static TechnicalIndicator mapJSONToTechnicalIndicator(String technicalIndicatorJSON) {
        JSONObject object;
        JSONTokener tokener = new JSONTokener(technicalIndicatorJSON);

        try {
            object = new JSONObject(tokener);
        } catch (JSONException e) {
            tokener = new JSONTokener(technicalIndicatorJSON);
            object = new JSONArray(tokener).getJSONObject(0);
        }

        JSONObject results = object.getJSONObject("results");

        String ticker = extractTicker(results);

        double value = 0.0;
        long timestamp = 0;

        JSONArray valuesArray = results.getJSONArray("values");
        if (!valuesArray.isEmpty()) {
            JSONObject valueObject = valuesArray.getJSONObject(0);
            value = valueObject.getDouble("value");
            timestamp = valueObject.getLong("timestamp");
        }

        LocalDate date = MapperHelper.convertTimestampToLocalDate(timestamp);

        return new TechnicalIndicator(ticker, value, date);
    }

    private static String extractTicker(JSONObject results) {
        String url = results.getJSONObject("underlying").getString("url");
        return url.split("/")[6];
    }
}