package com.cebix.investmenttrackerapp.mappers;

import com.cebix.investmenttrackerapp.datamodel.ExchangeStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class ExchangeStatusMapper {
    public static List<ExchangeStatus> mapJSONToExchangeStatus(String exchangeStatusJSON) {
        JSONObject object;
        JSONTokener tokener = new JSONTokener(exchangeStatusJSON);

        try {
            object = new JSONObject(tokener);
        } catch (JSONException e) {
            tokener = new JSONTokener(exchangeStatusJSON);
            object = new JSONArray(tokener).getJSONObject(0);
        }

        JSONObject exchanges = object.getJSONObject("exchanges");

        List<ExchangeStatus> exchangeStatuses = new ArrayList<>();

        for (String key : exchanges.keySet()) {
            String value = exchanges.getString(key).toLowerCase();

            if (!value.equals("open") && !value.equals("extended-hours") && !value.equals("closed")) {
                throw new JSONException("Invalid value: " + value);
            }

            boolean isOpen = value.equals("open") || value.equals("extended-hours");
            exchangeStatuses.add(new ExchangeStatus(key, isOpen));
        }

        return exchangeStatuses;
    }
}