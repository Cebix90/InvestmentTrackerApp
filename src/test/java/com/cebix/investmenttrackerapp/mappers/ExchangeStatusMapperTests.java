package com.cebix.investmenttrackerapp.mappers;

import com.cebix.investmenttrackerapp.datamodel.ExchangeStatus;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExchangeStatusMapperTests {
    @Test
    public void testMapJSONToExchangeStatus_IfCorrectData() throws IOException {
        String exchangeStatusJSON = new String(Files.readAllBytes(Paths.get("src/test/resources/mappers/exchangeStatus.json")));

        List<ExchangeStatus> exchangeStatuses = ExchangeStatusMapper.mapJSONToExchangeStatus(exchangeStatusJSON);

        assertNotNull(exchangeStatuses);

        assertEquals(3, exchangeStatuses.size());

        assertEquals("nasdaq", exchangeStatuses.get(0).getExchange());
        assertFalse(exchangeStatuses.get(0).isOpen());

        assertEquals("nyse", exchangeStatuses.get(1).getExchange());
        assertTrue(exchangeStatuses.get(1).isOpen());

        assertEquals("otc", exchangeStatuses.get(2).getExchange());
        assertTrue(exchangeStatuses.get(2).isOpen());
    }

    @Test
    public void testMapJSONToExchangeStatus_WithJSONArray() {
        String exchangeStatusJSONArray = "[{\"exchanges\": {\"nasdaq\": \"open\", \"nyse\": \"closed\"}}]";

        List<ExchangeStatus> exchangeStatuses = ExchangeStatusMapper.mapJSONToExchangeStatus(exchangeStatusJSONArray);

        assertNotNull(exchangeStatuses);

        assertEquals("nasdaq", exchangeStatuses.get(0).getExchange());
        assertTrue(exchangeStatuses.get(0).isOpen());

        assertEquals("nyse", exchangeStatuses.get(1).getExchange());
        assertFalse(exchangeStatuses.get(1).isOpen());
    }

    @Test
    public void testMapJSONToExchangeStatus_WithoutExchangesField() {
        String jsonWithoutExchangesField = "{ \"afterHours\": false }";

        assertThrows(JSONException.class, () -> ExchangeStatusMapper.mapJSONToExchangeStatus(jsonWithoutExchangesField));
    }

    @Test
    public void testMapJSONToExchangeStatus_WithoutCorrectValueForExchangeStatus() {
        String jsonWithoutCorrectValueForExchangeStatus = "[{\"exchanges\": {\"nasdaq\": \"open\", \"nyse\": \"somethingWrong\"}}]";

        assertThrows(JSONException.class, () -> ExchangeStatusMapper.mapJSONToExchangeStatus(jsonWithoutCorrectValueForExchangeStatus));
    }
}
